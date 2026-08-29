from fastapi import APIRouter

from app.services.vehicle_service import VEHICLES

router = APIRouter(prefix="/api/vehicles", tags=["vehicles"])


@router.get("")
def list_vehicles():
    return list(VEHICLES.values())
