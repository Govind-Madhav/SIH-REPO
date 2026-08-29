from pydantic import BaseModel
from fastapi import APIRouter

from app.services import notify_service

router = APIRouter(prefix="/api/notify", tags=["notify"])


class SubscribeInput(BaseModel):
    name: str
    phone: str
    district: str = "ALL"
    role: str = "traveller"
    channels: list[str] = ["sms"]


@router.get("/outbox")
def outbox():
    return notify_service.OUTBOX


@router.get("/subscribers")
def subscribers():
    return notify_service.SUBSCRIBERS


@router.post("/subscribe")
def subscribe(payload: SubscribeInput):
    return notify_service.register_subscriber(
        payload.name, payload.phone, payload.district, payload.role, payload.channels
    )
