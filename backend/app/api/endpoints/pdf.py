from fastapi import APIRouter

from app.api.endpoints.reports import generate_report as generate_structured_report

router = APIRouter()

@router.post("/generate")
async def generate_pdf(user_id: str):
    return {
        "status": "deprecated",
        "message": "Use POST /api/v1/reports instead.",
        "user_id": user_id,
    }

@router.get("/download/{file_name}")
async def download_pdf(file_name: str):
    return {
        "status": "deprecated",
        "message": "Use GET /api/v1/reports/{report_id}/download instead.",
        "file_name": file_name,
    }
