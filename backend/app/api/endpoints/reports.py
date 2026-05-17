import os
import time
from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel, Field

from app.services.auth_service import AuthenticatedUser, get_current_user
from app.services.logging_service import log_event
from app.services.profile_service import extract_snapshot_from_text
from app.services.report_service import generate_report_pdf
from app.services.stores import get_chat_session_store, get_report_store

router = APIRouter()


class ProfilingSnapshotResponse(BaseModel):
    business_idea: Optional[str] = None
    business_sector: Optional[str] = None
    preferred_company_type: Optional[str] = None
    experience_level: Optional[str] = None
    funding_need: Optional[str] = None
    legal_concerns: List[str] = Field(default_factory=list)


class GenerateReportRequest(BaseModel):
    session_id: str
    profile: Optional[ProfilingSnapshotResponse] = None
    user_id: Optional[str] = None


class RoadmapReportResponse(BaseModel):
    id: str
    user_id: str
    session_id: str
    title: str
    summary: str
    file_url: str
    generated_at: int
    approval_status: str = "IDLE"
    next_actions: List[str] = Field(default_factory=list)


@router.post("", response_model=RoadmapReportResponse)
async def generate_report(
    request: GenerateReportRequest,
    current_user: AuthenticatedUser = Depends(get_current_user),
):
    started_at = time.perf_counter()
    session_store = get_chat_session_store()
    report_store = get_report_store()
    session = session_store.get_session(current_user.uid, request.session_id)
    if not session:
        raise HTTPException(status_code=404, detail="Chat session not found.")

    report_id = f"report_{request.session_id}"
    existing_report = report_store.get_report(current_user.uid, report_id)
    if existing_report:
        log_event(
            "report.generate.idempotent_replay",
            uid=current_user.uid,
            session_id=request.session_id,
            report_id=report_id,
            latency_ms=round((time.perf_counter() - started_at) * 1000, 2),
        )
        return RoadmapReportResponse(**{key: value for key, value in existing_report.items() if key != "file_path" and key != "profile"})

    messages = session.get("messages", [])
    ai_messages = [message for message in messages if not message.get("is_from_user")]
    summary = ai_messages[-1]["text"] if ai_messages else "Henüz AI çıktısı oluşmadı."
    profile = request.profile or ProfilingSnapshotResponse(
        **extract_snapshot_from_text(summary).to_dict()
    )
    next_actions = [
        "Şirket kuruluş belgelerini hazırlayın",
        "Vergi avantajlarını kontrol edin",
        "Uzman onay simülasyonunu tamamlayın",
    ]
    file_path = generate_report_pdf(
        report_id=report_id,
        title="Girişim Hazırlık Raporu",
        summary=summary,
        next_actions=next_actions,
        session_messages=messages,
    )
    report = RoadmapReportResponse(
        id=report_id,
        user_id=current_user.uid,
        session_id=request.session_id,
        title="Girişim Hazırlık Raporu",
        summary=summary,
        file_url=f"/api/v1/reports/{report_id}/download",
        generated_at=int(time.time() * 1000),
        approval_status="IDLE",
        next_actions=next_actions,
    )
    payload = report.model_dump()
    payload["file_path"] = file_path
    payload["profile"] = profile.model_dump()
    report_store.save_report(current_user.uid, payload)
    session_store.update_session(
        current_user.uid,
        request.session_id,
        {
            "updatedAt": report.generated_at,
            "status": "report_generated",
            "relatedReportId": report.id,
        },
    )
    log_event(
        "report.generate.completed",
        uid=current_user.uid,
        session_id=request.session_id,
        report_id=report.id,
        latency_ms=round((time.perf_counter() - started_at) * 1000, 2),
    )
    return report


@router.get("/{report_id}", response_model=RoadmapReportResponse)
async def get_report_metadata(
    report_id: str,
    current_user: AuthenticatedUser = Depends(get_current_user),
):
    report = get_report_store().get_report(current_user.uid, report_id)
    if not report:
        raise HTTPException(status_code=404, detail="Report not found.")
    log_event("report.metadata.loaded", uid=current_user.uid, report_id=report_id)
    return RoadmapReportResponse(**{key: value for key, value in report.items() if key != "file_path" and key != "profile"})


@router.get("/{report_id}/download")
async def download_report(
    report_id: str,
    current_user: AuthenticatedUser = Depends(get_current_user),
):
    report = get_report_store().get_report(current_user.uid, report_id)
    if not report:
        raise HTTPException(status_code=404, detail="Report not found.")
    file_path = report["file_path"]
    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="Report file missing.")
    log_event("report.download.ready", uid=current_user.uid, report_id=report_id, file_path=file_path)
    return FileResponse(path=file_path, filename=f"{report_id}.pdf", media_type="application/pdf")
