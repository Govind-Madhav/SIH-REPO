"""
Simulates GPS-tracked logistics vehicles moving along the road network so the
dashboard has something live to show for "real-time movement and delivery
status of essential supplies". Positions are linearly interpolated between
the node coordinates of each vehicle's currently-planned (risk-aware) route.
"""

import random
import time
from datetime import datetime, timezone

from app.graph_data import NODES, EDGES
from app.services import routing_service
from app.services.simulation_service import broadcast

CARGO_TYPES = ["Medicines", "Food Supplies", "Construction Materials", "Agricultural Produce", "Fuel"]
DRIVER_NAMES = ["R. Sangma", "T. Lyngdoh", "P. Deka", "K. Marak", "L. Zeliang", "M. Chakma", "A. Konyak"]

VEHICLES: dict[str, dict] = {}
SOS_EVENTS: list[dict] = []

_node_pairs = None


def _all_node_pairs():
    global _node_pairs
    if _node_pairs is None:
        keys = list(NODES.keys())
        _node_pairs = [(a, b) for a in keys for b in keys if a != b]
    return _node_pairs


def _assign_new_trip(vehicle: dict):
    origin, destination = random.choice(_all_node_pairs())
    plan = routing_service.plan_routes(origin, destination, k=1)
    if "error" in plan or not plan.get("routes"):
        vehicle["route_nodes"] = [origin, destination]
    else:
        vehicle["route_nodes"] = plan["routes"][0]["path"]
    vehicle["origin"] = origin
    vehicle["destination"] = destination
    vehicle["origin_name"] = NODES[origin]["name"]
    vehicle["destination_name"] = NODES[destination]["name"]
    vehicle["segment_index"] = 0
    vehicle["segment_progress"] = 0.0
    vehicle["status"] = "MOVING"
    vehicle["lat"] = NODES[origin]["lat"]
    vehicle["lon"] = NODES[origin]["lon"]


def init_vehicles(n: int = 6):
    for i in range(n):
        vid = f"VEH-{i+1:03d}"
        vehicle = {
            "id": vid,
            "driver_name": random.choice(DRIVER_NAMES),
            "phone": f"+9199999{random.randint(10000, 99999)}",
            "cargo_type": CARGO_TYPES[i % len(CARGO_TYPES)],
            "speed_kmph": random.randint(30, 55),
        }
        _assign_new_trip(vehicle)
        VEHICLES[vid] = vehicle


def _lerp(a, b, t):
    return a + (b - a) * t


def tick_vehicles(dt_hours: float):
    for v in VEHICLES.values():
        if v["status"] in ("SOS", "BREAKDOWN"):
            continue

        path = v["route_nodes"]
        if len(path) < 2:
            _assign_new_trip(v)
            continue

        idx = v["segment_index"]
        if idx >= len(path) - 1:
            _assign_new_trip(v)
            continue

        a_key, b_key = path[idx], path[idx + 1]
        a, b = NODES[a_key], NODES[b_key]
        seg = next((e for e in EDGES if {e[0], e[1]} == {a_key, b_key}), None)
        distance_km = seg[2] if seg else 100

        distance_covered = v["speed_kmph"] * dt_hours
        progress_delta = distance_covered / max(1, distance_km)
        v["segment_progress"] += progress_delta

        if v["segment_progress"] >= 1.0:
            v["segment_index"] += 1
            v["segment_progress"] = 0.0
            if v["segment_index"] >= len(path) - 1:
                v["status"] = "DELIVERED"
                v["lat"], v["lon"] = b["lat"], b["lon"]
                broadcast({"kind": "vehicle_update", "data": v})
                continue
            a_key, b_key = path[v["segment_index"]], path[v["segment_index"] + 1]
            a, b = NODES[a_key], NODES[b_key]

        v["lat"] = round(_lerp(a["lat"], b["lat"], min(1.0, v["segment_progress"])), 5)
        v["lon"] = round(_lerp(a["lon"], b["lon"], min(1.0, v["segment_progress"])), 5)
        v["current_segment"] = f"{a_key}-{b_key}"
        broadcast({"kind": "vehicle_update", "data": v})


def trigger_sos(vehicle_id: str | None, driver_name: str, phone: str, lat: float, lon: float,
                 issue_type: str, message: str) -> dict:
    event = {
        "id": f"SOS-{int(time.time() * 1000)}",
        "vehicle_id": vehicle_id,
        "driver_name": driver_name,
        "phone": phone,
        "lat": lat,
        "lon": lon,
        "issue_type": issue_type,
        "message": message,
        "status": "OPEN",
        "created_at": datetime.now(timezone.utc).isoformat(),
    }
    SOS_EVENTS.insert(0, event)
    if vehicle_id and vehicle_id in VEHICLES:
        VEHICLES[vehicle_id]["status"] = "SOS"
    broadcast({"kind": "sos", "data": event})
    return event


def resolve_sos(sos_id: str) -> dict | None:
    for e in SOS_EVENTS:
        if e["id"] == sos_id:
            e["status"] = "RESOLVED"
            if e["vehicle_id"] and e["vehicle_id"] in VEHICLES:
                VEHICLES[e["vehicle_id"]]["status"] = "MOVING"
            broadcast({"kind": "sos_resolved", "data": e})
            return e
    return None
