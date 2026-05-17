from __future__ import annotations

import os
from dataclasses import dataclass

from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

from app.services.firebase_service import get_firebase_admin_app

security = HTTPBearer(auto_error=True)


@dataclass(frozen=True)
class AuthenticatedUser:
    uid: str
    email: str | None = None


def verify_bearer_token(token: str) -> AuthenticatedUser:
    auth_mode = os.getenv("GK_AUTH_MODE", "firebase").strip().lower()
    if auth_mode == "testing":
        if not token.startswith("test-uid:"):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid test token.",
            )
        uid = token.removeprefix("test-uid:").strip()
        if not uid:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Missing UID in test token.",
            )
        return AuthenticatedUser(uid=uid, email=f"{uid}@example.test")

    try:
        from firebase_admin import auth
    except ImportError as exc:  # pragma: no cover - runtime dependency
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Firebase Admin SDK is not installed on the backend.",
        ) from exc

    try:
        app = get_firebase_admin_app()
        claims = auth.verify_id_token(token, app=app)
    except Exception as exc:  # pragma: no cover - external service
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired Firebase ID token.",
        ) from exc

    return AuthenticatedUser(uid=claims["uid"], email=claims.get("email"))


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security),
) -> AuthenticatedUser:
    return verify_bearer_token(credentials.credentials)
