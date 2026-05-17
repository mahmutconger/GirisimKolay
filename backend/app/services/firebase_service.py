from __future__ import annotations

import json
import os
from functools import lru_cache


def _service_account_info() -> dict | None:
    raw_json = os.getenv("FIREBASE_SERVICE_ACCOUNT_JSON", "").strip()
    if raw_json:
        return json.loads(raw_json)

    client_email = os.getenv("FIREBASE_CLIENT_EMAIL", "").strip()
    private_key = os.getenv("FIREBASE_PRIVATE_KEY", "").strip()
    project_id = os.getenv("FIREBASE_PROJECT_ID", "").strip()
    if client_email and private_key:
        return {
            "type": "service_account",
            "project_id": project_id,
            "client_email": client_email,
            "private_key": private_key.replace("\\n", "\n"),
            "token_uri": "https://oauth2.googleapis.com/token",
        }
    return None


@lru_cache(maxsize=1)
def get_firebase_admin_app():
    try:
        import firebase_admin
        from firebase_admin import credentials
    except ImportError as exc:  # pragma: no cover - runtime dependency
        raise RuntimeError("firebase-admin is required for production auth and Firestore writes.") from exc

    if firebase_admin._apps:
        return firebase_admin.get_app()

    options = {}
    project_id = os.getenv("FIREBASE_PROJECT_ID", "").strip()
    if project_id:
        options["projectId"] = project_id

    service_account_path = os.getenv("FIREBASE_SERVICE_ACCOUNT_PATH", "").strip()
    service_account_info = _service_account_info()

    if service_account_path:
        credential = credentials.Certificate(service_account_path)
        return firebase_admin.initialize_app(credential, options=options)
    if service_account_info:
        credential = credentials.Certificate(service_account_info)
        return firebase_admin.initialize_app(credential, options=options)

    credential = credentials.ApplicationDefault()
    return firebase_admin.initialize_app(credential, options=options)


@lru_cache(maxsize=1)
def get_firestore_client():
    try:
        from google.auth.credentials import AnonymousCredentials
        from google.cloud import firestore
        from google.oauth2 import service_account
    except ImportError as exc:  # pragma: no cover - runtime dependency
        raise RuntimeError("google-cloud-firestore is required for production persistence.") from exc

    project_id = os.getenv("FIREBASE_PROJECT_ID", "").strip()
    emulator_host = os.getenv("FIRESTORE_EMULATOR_HOST", "").strip()
    service_account_path = os.getenv("FIREBASE_SERVICE_ACCOUNT_PATH", "").strip()
    service_account_info = _service_account_info()

    if emulator_host:
        return firestore.Client(
            project=project_id or "girisimkolay-local",
            credentials=AnonymousCredentials(),
        )
    if service_account_path:
        credentials = service_account.Credentials.from_service_account_file(service_account_path)
        return firestore.Client(project=project_id or credentials.project_id, credentials=credentials)
    if service_account_info:
        credentials = service_account.Credentials.from_service_account_info(service_account_info)
        return firestore.Client(project=project_id or credentials.project_id, credentials=credentials)

    return firestore.Client(project=project_id or None)
