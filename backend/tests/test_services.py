import os
import shutil
import tempfile
import unittest

from app.services import rag_service
from app.services.profile_service import extract_snapshot_from_text
from app.services.report_service import REPORTS_DIR, generate_report_pdf


class RagServiceTests(unittest.TestCase):
    def test_retrieve_returns_structured_citations(self):
        results = rag_service.retrieve("mikro ihracat için ETGB şartları nelerdir?", top_k=2)
        self.assertGreaterEqual(len(results), 1)
        self.assertIn("source_name", results[0])
        self.assertIn("section", results[0])

    def test_build_context_returns_citations(self):
        context, citations = rag_service.build_context("KOSGEB hibe desteği")
        self.assertTrue(context)
        self.assertTrue(citations)
        self.assertIn("source_name", citations[0])


class ProfileServiceTests(unittest.TestCase):
    def test_extract_snapshot_detects_sector_and_company_type(self):
        snapshot = extract_snapshot_from_text("E-ticaret yapan yeni mezun olarak şahıs şirketi kurmak istiyorum.")
        self.assertEqual(snapshot.business_sector, "E-Ticaret")
        self.assertEqual(snapshot.preferred_company_type, "Şahıs Şirketi")


class ReportServiceTests(unittest.TestCase):
    def setUp(self):
        self.original_dir = REPORTS_DIR
        self.temp_dir = tempfile.mkdtemp()
        report_service_module = __import__("app.services.report_service", fromlist=["REPORTS_DIR"])
        report_service_module.REPORTS_DIR = report_service_module.Path(self.temp_dir)
        self.report_service_module = report_service_module

    def tearDown(self):
        self.report_service_module.REPORTS_DIR = self.original_dir
        shutil.rmtree(self.temp_dir, ignore_errors=True)

    def test_generate_report_pdf_creates_file(self):
        file_path = generate_report_pdf(
            report_id="test-report",
            title="Test Raporu",
            summary="Özet",
            next_actions=["Adım 1", "Adım 2"],
            session_messages=[{"text": "Merhaba", "is_from_user": True}],
        )
        self.assertTrue(os.path.exists(file_path))


if __name__ == "__main__":
    unittest.main()
