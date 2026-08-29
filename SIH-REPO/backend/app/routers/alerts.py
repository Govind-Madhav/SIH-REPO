from fastapi import APIRouter

from app.services.simulation_service import ALERTS

router = APIRouter(prefix="/api/alerts", tags=["alerts"])


@router.get("")
def list_alerts(active_only: bool = False):
    if active_only:
        return [a for a in ALERTS if a.get("active")]
    return ALERTS
