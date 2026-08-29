from fastapi import APIRouter, HTTPException

from app.services.simulation_service import STATE

router = APIRouter(prefix="/api/sensors", tags=["sensors"])


@router.get("")
def list_sensors():
    return list(STATE.values())


@router.get("/{node_key}")
def get_sensor(node_key: str):
    node_key = node_key.upper()
    if node_key not in STATE:
        raise HTTPException(404, f"Unknown node '{node_key}'")
    return STATE[node_key]
