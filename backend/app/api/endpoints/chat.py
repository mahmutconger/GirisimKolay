import os
import time
from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from google import genai
from google.genai import types
from pydantic import BaseModel, Field

from app.services import rag_service
from app.services.auth_service import AuthenticatedUser, get_current_user
from app.services.logging_service import log_event
from app.services.profile_service import extract_snapshot_from_text
from app.services.stores import get_chat_session_store

router = APIRouter()

_GEMINI_KEY = os.getenv("GEMINI_API_KEY", "")
_GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-1.5-pro")

_SYSTEM_PROMPT = """Sen GirişimKolay'ın yapay zeka iş danışmanısın.
Sana verilen mevzuat bağlamını kullanarak Türkiye'deki girişimcilere somut,
pratik ve kaynak gösterilmiş tavsiyeler ver.

Kurallar:
- Daima Türkçe cevap ver.
- Cevabın kısa ve uygulanabilir olsun.
- Verilen mevzuat bağlamına sadık kal; bağlamda olmayan bilgiyi uydurma.
- Delil yetersizse bunu açıkça belirt ve tahmin yürütme.
- Motive edici ve samimi bir dil kullan."""


def _get_client():
    if not _GEMINI_KEY:
        return None
    return genai.Client(api_key=_GEMINI_KEY)


class CitationResponse(BaseModel):
    source_name: str
    section: Optional[str] = None
    snippet: Optional[str] = None
    source_url: Optional[str] = None


class ProfilingSnapshotResponse(BaseModel):
    business_idea: Optional[str] = None
    business_sector: Optional[str] = None
    preferred_company_type: Optional[str] = None
    experience_level: Optional[str] = None
    funding_need: Optional[str] = None
    legal_concerns: List[str] = Field(default_factory=list)


class MessageRequest(BaseModel):
    text: str
    session_id: Optional[str] = None
    client_request_id: str
    user_id: Optional[str] = None


class ChatMessageResponse(BaseModel):
    id: str
    session_id: str
    text: str
    is_from_user: bool
    timestamp: int
    citations: List[CitationResponse] = Field(default_factory=list)
    profile_delta: Optional[ProfilingSnapshotResponse] = None
    confidence: Optional[float] = None
    next_actions: List[str] = Field(default_factory=list)


class SendMessageResponse(BaseModel):
    session_id: str
    message: ChatMessageResponse
    answer: str
    citations: List[CitationResponse]
    profile_delta: Optional[ProfilingSnapshotResponse] = None
    confidence: float
    next_actions: List[str]
    insufficient_evidence: bool = False


class ChatSessionResponse(BaseModel):
    session_id: str
    messages: List[ChatMessageResponse]


def _message_from_store(raw_message: dict) -> ChatMessageResponse:
    return ChatMessageResponse(**raw_message)


@router.post("/messages", response_model=SendMessageResponse)
async def send_message(
    request: MessageRequest,
    current_user: AuthenticatedUser = Depends(get_current_user),
):
    now_ms = int(time.time() * 1000)
    started_at = time.perf_counter()
    session_id = request.session_id or f"session_{current_user.uid}_{now_ms}"
    store = get_chat_session_store()
    store.get_or_create_session(
        uid=current_user.uid,
        session_id=session_id,
        session_seed={
            "id": session_id,
            "uid": current_user.uid,
            "title": request.text[:80],
            "status": "active",
            "createdAt": now_ms,
            "updatedAt": now_ms,
            "lastMessageAt": now_ms,
            "latestProfileSnapshot": None,
        },
    )

    existing_ai_message = store.get_message(
        uid=current_user.uid,
        session_id=session_id,
        message_id=f"ai_{request.client_request_id}",
    )
    if existing_ai_message:
        ai_payload = ChatMessageResponse(**existing_ai_message)
        log_event(
            "chat.message.idempotent_replay",
            uid=current_user.uid,
            session_id=session_id,
            request_id=request.client_request_id,
            latency_ms=round((time.perf_counter() - started_at) * 1000, 2),
        )
        return SendMessageResponse(
            session_id=session_id,
            message=ai_payload,
            answer=ai_payload.text,
            citations=ai_payload.citations,
            profile_delta=ai_payload.profile_delta,
            confidence=ai_payload.confidence or 0.0,
            next_actions=ai_payload.next_actions,
            insufficient_evidence=(ai_payload.confidence or 0.0) < 0.12,
        )

    user_message = {
        "id": f"user_{request.client_request_id}",
        "session_id": session_id,
        "text": request.text,
        "is_from_user": True,
        "timestamp": now_ms,
        "citations": [],
        "profile_delta": None,
        "confidence": None,
        "next_actions": [],
        "requestId": request.client_request_id,
        "role": "user",
    }
    store.save_message(current_user.uid, session_id, user_message)

    context_block, rag_sources = rag_service.build_context(request.text)
    profile_delta = extract_snapshot_from_text(request.text)
    citations = [CitationResponse(**source) for source in rag_sources]
    confidence = round(sum(source.get("score", 0.0) for source in rag_sources) / max(len(rag_sources), 1), 2) if rag_sources else 0.15
    insufficient_evidence = not rag_sources or confidence < 0.12
    next_actions = _build_next_actions(profile_delta)

    client = _get_client()
    if client:
        try:
            prompt = ""
            if context_block:
                prompt += f"{context_block}\n\n---\n\n"
            prompt += f"Kullanıcı sorusu: {request.text}\n"
            prompt += f"Profil çıkarımı: {profile_delta.to_dict()}"

            response = client.models.generate_content(
                model=_GEMINI_MODEL,
                config=types.GenerateContentConfig(system_instruction=_SYSTEM_PROMPT),
                contents=prompt
            )
            raw_text = response.text.strip()
            ai_payload = ChatMessageResponse(
                id=f"ai_{request.client_request_id}",
                session_id=session_id,
                text=raw_text,
                is_from_user=False,
                timestamp=now_ms,
                citations=citations,
                profile_delta=ProfilingSnapshotResponse(**profile_delta.to_dict()),
                confidence=confidence,
                next_actions=next_actions,
            )
            store.save_message(current_user.uid, session_id, {
                **ai_payload.model_dump(),
                "requestId": request.client_request_id,
                "role": "assistant",
            })
            store.update_session(
                current_user.uid,
                session_id,
                {
                    "updatedAt": now_ms,
                    "lastMessageAt": now_ms,
                    "latestProfileSnapshot": profile_delta.to_dict(),
                },
            )
            log_event(
                "chat.message.completed",
                uid=current_user.uid,
                session_id=session_id,
                request_id=request.client_request_id,
                citations=len(citations),
                confidence=confidence,
                insufficient_evidence=insufficient_evidence,
                latency_ms=round((time.perf_counter() - started_at) * 1000, 2),
            )
            return SendMessageResponse(
                session_id=session_id,
                message=ai_payload,
                answer=raw_text,
                citations=citations,
                profile_delta=ai_payload.profile_delta,
                confidence=confidence,
                next_actions=next_actions,
                insufficient_evidence=insufficient_evidence,
            )
        except Exception as e:
            log_event(
                "chat.message.model_fallback",
                uid=current_user.uid,
                session_id=session_id,
                request_id=request.client_request_id,
                error_type=type(e).__name__,
                latency_ms=round((time.perf_counter() - started_at) * 1000, 2),
            )

    rag_results = rag_service.retrieve(request.text, top_k=1)
    if rag_results:
        best = rag_results[0]
        sentences = best["text"].split(". ")
        summary = ". ".join(sentences[:2]) + "."
        ai_text = summary.strip()
    else:
        ai_text = (
            "Sorunuz için elimde yeterli doğrulanmış mevzuat bağlamı yok. "
            "Sektör, hedef müşteri ve şirket tipi tercihinizi paylaşırsanız güvenli bir yol haritası çıkarabilirim."
        )

    ai_payload = ChatMessageResponse(
        id=f"ai_{request.client_request_id}",
        session_id=session_id,
        text=ai_text,
        is_from_user=False,
        timestamp=now_ms,
        citations=citations,
        profile_delta=ProfilingSnapshotResponse(**profile_delta.to_dict()),
        confidence=confidence,
        next_actions=next_actions,
    )
    store.save_message(current_user.uid, session_id, {
        **ai_payload.model_dump(),
        "requestId": request.client_request_id,
        "role": "assistant",
    })
    store.update_session(
        current_user.uid,
        session_id,
        {
            "updatedAt": now_ms,
            "lastMessageAt": now_ms,
            "latestProfileSnapshot": profile_delta.to_dict(),
        },
    )
    log_event(
        "chat.message.completed",
        uid=current_user.uid,
        session_id=session_id,
        request_id=request.client_request_id,
        citations=len(citations),
        confidence=confidence,
        insufficient_evidence=insufficient_evidence,
        latency_ms=round((time.perf_counter() - started_at) * 1000, 2),
    )
    return SendMessageResponse(
        session_id=session_id,
        message=ai_payload,
        answer=ai_text,
        citations=citations,
        profile_delta=ai_payload.profile_delta,
        confidence=confidence,
        next_actions=next_actions,
        insufficient_evidence=insufficient_evidence,
    )


@router.get("/sessions/{session_id}", response_model=ChatSessionResponse)
async def get_chat_history(
    session_id: str,
    current_user: AuthenticatedUser = Depends(get_current_user),
):
    session = get_chat_session_store().get_session(current_user.uid, session_id)
    if not session:
        raise HTTPException(status_code=404, detail="Chat session not found.")
    log_event(
        "chat.session.loaded",
        uid=current_user.uid,
        session_id=session_id,
        message_count=len(session.get("messages", [])),
    )
    return ChatSessionResponse(
        session_id=session_id,
        messages=[_message_from_store(item) for item in session.get("messages", [])],
    )


def _build_next_actions(profile_delta) -> List[str]:
    actions = ["Şirket türünüzü netleştirin", "Vergi ve mevzuat yükümlülüklerini kontrol edin"]
    if profile_delta.funding_need:
        actions.append("İlgili hibe ve teşvik programlarını filtreleyin")
    return actions[:3]
