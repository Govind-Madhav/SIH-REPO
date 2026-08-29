"""
WhatsApp + SMS dispatch via Twilio, with graceful degradation:

- No Twilio credentials in .env  -> every "send" is logged and stored in
  OUTBOX with status "SIMULATED" (the whole app still works end-to-end).
- Credentials present but the send fails (no signal / API error)  -> the
  message is stored with status "QUEUED_OFFLINE" and a background retry
  loop (see `flush_outbox_forever`) keeps trying, modelling the "sync once
  back in network coverage" requirement from the field.
- Credentials present and the send succeeds -> status "SENT".

A tiny in-memory phonebook (SUBSCRIBERS) stands in for a real registered-user
database: transport/logistics drivers, local authorities and general
travellers who opted in per district get notified when that district's
route risk crosses HIGH.
"""

import os
import asyncio
import itertools
from datetime import datetime, timezone

_id_counter = itertools.count(1)

TWILIO_ACCOUNT_SID = os.getenv("TWILIO_ACCOUNT_SID", "").strip()
TWILIO_AUTH_TOKEN = os.getenv("TWILIO_AUTH_TOKEN", "").strip()
TWILIO_SMS_FROM = os.getenv("TWILIO_SMS_FROM_NUMBER", "").strip()
TWILIO_WHATSAPP_FROM = os.getenv("TWILIO_WHATSAPP_FROM_NUMBER", "whatsapp:+14155238886").strip()

_twilio_client = None
if TWILIO_ACCOUNT_SID and TWILIO_AUTH_TOKEN:
    try:
        from twilio.rest import Client
        _twilio_client = Client(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN)
    except Exception:
        _twilio_client = None

OUTBOX: list[dict] = []

# Dummy opt-in phonebook for the demo. Register more via POST /api/notify/subscribe.
SUBSCRIBERS: list[dict] = [
    {"name": "Demo Logistics Dispatch", "phone": "+919999900001", "district": "ALL", "role": "logistics", "channels": ["whatsapp", "sms"]},
    {"name": "Demo District Authority - Ri-Bhoi", "phone": "+919999900002", "district": "Ri-Bhoi", "role": "authority", "channels": ["sms"]},
    {"name": "Demo Traveller", "phone": "+919999900003", "district": "ALL", "role": "traveller", "channels": ["sms"]},
]


def _log(entry: dict):
    OUTBOX.insert(0, entry)
    del OUTBOX[300:]


def _record(channel: str, to: str, body: str, status: str, error: str | None = None) -> dict:
    entry = {
        "id": f"MSG-{next(_id_counter):06d}-{channel}-{to[-4:]}",
        "channel": channel,
        "to": to,
        "body": body,
        "status": status,
        "error": error,
        "created_at": datetime.now(timezone.utc).isoformat(),
        "attempts": 1,
    }
    _log(entry)
    return entry


def send_sms(to: str, body: str) -> dict:
    if not _twilio_client or not TWILIO_SMS_FROM:
        return _record("sms", to, body, "SIMULATED")
    try:
        _twilio_client.messages.create(to=to, from_=TWILIO_SMS_FROM, body=body)
        return _record("sms", to, body, "SENT")
    except Exception as exc:
        return _record("sms", to, body, "QUEUED_OFFLINE", error=str(exc))


def send_whatsapp(to: str, body: str) -> dict:
    if not _twilio_client:
        return _record("whatsapp", to, body, "SIMULATED")
    try:
        to_wa = to if to.startswith("whatsapp:") else f"whatsapp:{to}"
        _twilio_client.messages.create(to=to_wa, from_=TWILIO_WHATSAPP_FROM, body=body)
        return _record("whatsapp", to, body, "SENT")
    except Exception as exc:
        return _record("whatsapp", to, body, "QUEUED_OFFLINE", error=str(exc))


def register_subscriber(name: str, phone: str, district: str, role: str, channels: list[str]) -> dict:
    entry = {"name": name, "phone": phone, "district": district, "role": role, "channels": channels}
    SUBSCRIBERS.append(entry)
    return entry


async def broadcast_route_danger(alert: dict):
    """Notify everyone subscribed to this district (or ALL) via their preferred channels."""
    targets = [s for s in SUBSCRIBERS if s["district"] in (alert.get("district"), "ALL")]
    text = (
        f"[NER LogiSense ALERT] {alert.get('category', 'HIGH')} risk near {alert.get('node_name', alert.get('district'))}. "
        f"{alert.get('message', '')} Avoid this route if possible; alternate routing has been suggested in-app."
    )
    for sub in targets:
        if "whatsapp" in sub["channels"]:
            send_whatsapp(sub["phone"], text)
        if "sms" in sub["channels"]:
            send_sms(sub["phone"], text)
    await asyncio.sleep(0)


async def flush_outbox_forever(interval_seconds: float = 20.0):
    """Retries QUEUED_OFFLINE messages -- simulates a device syncing once it regains signal."""
    while True:
        await asyncio.sleep(interval_seconds)
        pending = [m for m in OUTBOX if m["status"] == "QUEUED_OFFLINE"]
        for m in pending:
            if _twilio_client:
                try:
                    if m["channel"] == "sms" and TWILIO_SMS_FROM:
                        _twilio_client.messages.create(to=m["to"], from_=TWILIO_SMS_FROM, body=m["body"])
                    elif m["channel"] == "whatsapp":
                        to_wa = m["to"] if m["to"].startswith("whatsapp:") else f"whatsapp:{m['to']}"
                        _twilio_client.messages.create(to=to_wa, from_=TWILIO_WHATSAPP_FROM, body=m["body"])
                    m["status"] = "SENT"
                except Exception as exc:
                    m["attempts"] += 1
                    m["error"] = str(exc)
