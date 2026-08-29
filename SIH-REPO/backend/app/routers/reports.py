import os
import uuid

from fastapi import APIRouter, Form, UploadFile, File

from app.services import reports_service

router = APIRouter(prefix="/api/reports", tags=["reports"])

UPLOAD_DIR = os.path.join(os.path.dirname(__file__), "..", "..", "uploads")
os.makedirs(UPLOAD_DIR, exist_ok=True)


@router.get("")
def list_reports():
    return reports_service.REPORTS


@router.post("")
async def create_report(
    reporter_name: str = Form(...),
    phone: str = Form(""),
    incident_type: str = Form(...),
    description: str = Form(""),
    lat: float = Form(...),
    lon: float = Form(...),
    photo: UploadFile | None = File(None),
):
    photo_path = None
    if photo is not None and photo.filename:
        ext = os.path.splitext(photo.filename)[1] or ".jpg"
        fname = f"{uuid.uuid4().hex}{ext}"
        dest = os.path.join(UPLOAD_DIR, fname)
        with open(dest, "wb") as f:
            f.write(await photo.read())
        photo_path = f"/uploads/{fname}"

    report = await reports_service.submit_report(
        reporter_name, phone, incident_type, description, lat, lon, photo_path
    )
    return report
