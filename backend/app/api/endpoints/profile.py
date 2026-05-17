from typing import Dict, List, Optional

from fastapi import APIRouter, Depends
from pydantic import BaseModel, Field

from app.services.auth_service import AuthenticatedUser, get_current_user
from app.services.profile_service import extract_snapshot_from_text

router = APIRouter()


class ProfilingSnapshotResponse(BaseModel):
    business_idea: Optional[str] = None
    business_sector: Optional[str] = None
    preferred_company_type: Optional[str] = None
    experience_level: Optional[str] = None
    funding_need: Optional[str] = None
    legal_concerns: List[str] = Field(default_factory=list)


class ProfileExtractRequest(BaseModel):
    text: str
    current_profile: Optional[Dict[str, object]] = None


class ProfileExtractResponse(BaseModel):
    snapshot: ProfilingSnapshotResponse


@router.post("/extract", response_model=ProfileExtractResponse)
async def extract_profile(
    request: ProfileExtractRequest,
    current_user: AuthenticatedUser = Depends(get_current_user),
):
    snapshot = extract_snapshot_from_text(
        text=request.text,
        current_snapshot=request.current_profile,
    )
    return ProfileExtractResponse(snapshot=ProfilingSnapshotResponse(**snapshot.to_dict()))
