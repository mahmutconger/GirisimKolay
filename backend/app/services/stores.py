from __future__ import annotations

import os
from abc import ABC, abstractmethod
from copy import deepcopy
from threading import Lock
from typing import Any

from app.services.firebase_service import get_firestore_client


class BaseChatSessionStore(ABC):
    @abstractmethod
    def get_or_create_session(self, uid: str, session_id: str, session_seed: dict[str, Any]) -> dict[str, Any]:
        raise NotImplementedError

    @abstractmethod
    def get_session(self, uid: str, session_id: str) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    def get_message(self, uid: str, session_id: str, message_id: str) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    def save_message(self, uid: str, session_id: str, message: dict[str, Any]) -> None:
        raise NotImplementedError

    @abstractmethod
    def update_session(self, uid: str, session_id: str, session_data: dict[str, Any]) -> None:
        raise NotImplementedError


class BaseReportStore(ABC):
    @abstractmethod
    def get_report(self, uid: str, report_id: str) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    def save_report(self, uid: str, report: dict[str, Any]) -> None:
        raise NotImplementedError


class MemoryChatSessionStore(BaseChatSessionStore):
    def __init__(self) -> None:
        self._sessions: dict[str, dict[str, dict[str, Any]]] = {}
        self._messages: dict[tuple[str, str], dict[str, dict[str, Any]]] = {}
        self._lock = Lock()

    def get_or_create_session(self, uid: str, session_id: str, session_seed: dict[str, Any]) -> dict[str, Any]:
        with self._lock:
            user_sessions = self._sessions.setdefault(uid, {})
            if session_id not in user_sessions:
                user_sessions[session_id] = deepcopy(session_seed)
            return deepcopy(user_sessions[session_id])

    def get_session(self, uid: str, session_id: str) -> dict[str, Any] | None:
        with self._lock:
            session = self._sessions.get(uid, {}).get(session_id)
            if not session:
                return None
            messages = list(self._messages.get((uid, session_id), {}).values())
            messages.sort(key=lambda item: item["timestamp"])
            return deepcopy({**session, "messages": messages})

    def get_message(self, uid: str, session_id: str, message_id: str) -> dict[str, Any] | None:
        with self._lock:
            message = self._messages.get((uid, session_id), {}).get(message_id)
            return deepcopy(message) if message else None

    def save_message(self, uid: str, session_id: str, message: dict[str, Any]) -> None:
        with self._lock:
            self._messages.setdefault((uid, session_id), {})[message["id"]] = deepcopy(message)

    def update_session(self, uid: str, session_id: str, session_data: dict[str, Any]) -> None:
        with self._lock:
            self._sessions.setdefault(uid, {}).setdefault(session_id, {}).update(deepcopy(session_data))

    def clear(self) -> None:
        with self._lock:
            self._sessions.clear()
            self._messages.clear()


class MemoryReportStore(BaseReportStore):
    def __init__(self) -> None:
        self._reports: dict[str, dict[str, dict[str, Any]]] = {}
        self._lock = Lock()

    def get_report(self, uid: str, report_id: str) -> dict[str, Any] | None:
        with self._lock:
            report = self._reports.get(uid, {}).get(report_id)
            return deepcopy(report) if report else None

    def save_report(self, uid: str, report: dict[str, Any]) -> None:
        with self._lock:
            self._reports.setdefault(uid, {})[report["id"]] = deepcopy(report)

    def clear(self) -> None:
        with self._lock:
            self._reports.clear()


class FirestoreChatSessionStore(BaseChatSessionStore):
    def __init__(self) -> None:
        self._client = get_firestore_client()

    def get_or_create_session(self, uid: str, session_id: str, session_seed: dict[str, Any]) -> dict[str, Any]:
        session_ref = self._session_ref(uid, session_id)
        snapshot = session_ref.get()
        if not snapshot.exists:
            session_ref.set(session_seed)
            return deepcopy(session_seed)
        return snapshot.to_dict() or {}

    def get_session(self, uid: str, session_id: str) -> dict[str, Any] | None:
        session_ref = self._session_ref(uid, session_id)
        snapshot = session_ref.get()
        if not snapshot.exists:
            return None
        messages = [doc.to_dict() for doc in session_ref.collection("messages").order_by("timestamp").stream()]
        return {**(snapshot.to_dict() or {}), "messages": messages}

    def get_message(self, uid: str, session_id: str, message_id: str) -> dict[str, Any] | None:
        snapshot = self._message_ref(uid, session_id, message_id).get()
        return snapshot.to_dict() if snapshot.exists else None

    def save_message(self, uid: str, session_id: str, message: dict[str, Any]) -> None:
        self._message_ref(uid, session_id, message["id"]).set(message)

    def update_session(self, uid: str, session_id: str, session_data: dict[str, Any]) -> None:
        self._session_ref(uid, session_id).set(session_data, merge=True)

    def _session_ref(self, uid: str, session_id: str):
        return self._client.collection("users").document(uid).collection("chatSessions").document(session_id)

    def _message_ref(self, uid: str, session_id: str, message_id: str):
        return self._session_ref(uid, session_id).collection("messages").document(message_id)


class FirestoreReportStore(BaseReportStore):
    def __init__(self) -> None:
        self._client = get_firestore_client()

    def get_report(self, uid: str, report_id: str) -> dict[str, Any] | None:
        snapshot = self._report_ref(uid, report_id).get()
        return snapshot.to_dict() if snapshot.exists else None

    def save_report(self, uid: str, report: dict[str, Any]) -> None:
        self._report_ref(uid, report["id"]).set(report)

    def _report_ref(self, uid: str, report_id: str):
        return self._client.collection("users").document(uid).collection("reports").document(report_id)


_memory_chat_store = MemoryChatSessionStore()
_memory_report_store = MemoryReportStore()
_firestore_chat_store: FirestoreChatSessionStore | None = None
_firestore_report_store: FirestoreReportStore | None = None


def get_chat_session_store() -> BaseChatSessionStore:
    global _firestore_chat_store
    backend = os.getenv("GK_PERSISTENCE_BACKEND", "firestore").strip().lower()
    if backend == "memory":
        return _memory_chat_store
    if _firestore_chat_store is None:
        _firestore_chat_store = FirestoreChatSessionStore()
    return _firestore_chat_store


def get_report_store() -> BaseReportStore:
    global _firestore_report_store
    backend = os.getenv("GK_PERSISTENCE_BACKEND", "firestore").strip().lower()
    if backend == "memory":
        return _memory_report_store
    if _firestore_report_store is None:
        _firestore_report_store = FirestoreReportStore()
    return _firestore_report_store


def reset_memory_stores() -> None:
    _memory_chat_store.clear()
    _memory_report_store.clear()
