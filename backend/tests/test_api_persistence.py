import os
import shutil
import tempfile
import unittest
import httpx
import asyncio

os.environ["GK_AUTH_MODE"] = "testing"
os.environ["GK_PERSISTENCE_BACKEND"] = "memory"

from app.main import app
from app.services.report_service import REPORTS_DIR
from app.services.stores import reset_memory_stores


class ApiPersistenceTests(unittest.TestCase):
    def setUp(self):
        reset_memory_stores()
        self.auth_header = {"Authorization": "Bearer test-uid:user-1"}
        self.temp_dir = tempfile.mkdtemp()
        report_service_module = __import__("app.services.report_service", fromlist=["REPORTS_DIR"])
        self.original_reports_dir = REPORTS_DIR
        report_service_module.REPORTS_DIR = report_service_module.Path(self.temp_dir)
        self.report_service_module = report_service_module

    def tearDown(self):
        self.report_service_module.REPORTS_DIR = self.original_reports_dir
        shutil.rmtree(self.temp_dir, ignore_errors=True)

    def request(self, method: str, path: str, **kwargs):
        async def _send():
            transport = httpx.ASGITransport(app=app)
            async with httpx.AsyncClient(transport=transport, base_url="http://testserver") as client:
                return await client.request(method, path, **kwargs)

        return asyncio.run(_send())

    def test_chat_requires_auth(self):
        response = self.request(
            "POST",
            "/api/v1/chat/messages",
            json={"text": "Merhaba", "client_request_id": "req-1"},
        )
        self.assertEqual(response.status_code, 403)

    def test_chat_persists_session_and_is_idempotent(self):
        payload = {
            "text": "Şahıs şirketi kurmak istiyorum.",
            "client_request_id": "req-1",
        }
        first_response = self.request("POST", "/api/v1/chat/messages", json=payload, headers=self.auth_header)
        self.assertEqual(first_response.status_code, 200)
        first_body = first_response.json()
        session_id = first_body["session_id"]

        second_response = self.request(
            "POST",
            "/api/v1/chat/messages",
            json={**payload, "session_id": session_id},
            headers=self.auth_header,
        )
        self.assertEqual(second_response.status_code, 200)
        self.assertEqual(second_response.json()["message"]["id"], first_body["message"]["id"])

        history_response = self.request("GET", f"/api/v1/chat/sessions/{session_id}", headers=self.auth_header)
        self.assertEqual(history_response.status_code, 200)
        history = history_response.json()["messages"]
        self.assertEqual(len(history), 2)
        self.assertEqual(history[0]["id"], "user_req-1")
        self.assertEqual(history[1]["id"], "ai_req-1")

    def test_report_generation_is_persistent_and_owner_scoped(self):
        chat_response = self.request(
            "POST",
            "/api/v1/chat/messages",
            json={"text": "KOSGEB hibesi için yol haritası çıkar.", "client_request_id": "req-2"},
            headers=self.auth_header,
        )
        session_id = chat_response.json()["session_id"]

        first_report = self.request(
            "POST",
            "/api/v1/reports",
            json={"session_id": session_id},
            headers=self.auth_header,
        )
        self.assertEqual(first_report.status_code, 200)
        first_body = first_report.json()

        second_report = self.request(
            "POST",
            "/api/v1/reports",
            json={"session_id": session_id},
            headers=self.auth_header,
        )
        self.assertEqual(second_report.status_code, 200)
        self.assertEqual(second_report.json()["id"], first_body["id"])

        report_metadata = self.request("GET", f"/api/v1/reports/{first_body['id']}", headers=self.auth_header)
        self.assertEqual(report_metadata.status_code, 200)
        self.assertEqual(report_metadata.json()["session_id"], session_id)

        forbidden_report = self.request(
            "GET",
            f"/api/v1/reports/{first_body['id']}",
            headers={"Authorization": "Bearer test-uid:user-2"},
        )
        self.assertEqual(forbidden_report.status_code, 404)


if __name__ == "__main__":
    unittest.main()
