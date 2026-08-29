"""
Generates a synthetic (dummy) sensor + weather dataset for training the
landslide risk ML model used by the NER Smart Logistics platform.

Physical intuition baked into the synthetic generator (matches the project's
hypothesis): heavy recent rainfall increases soil moisture; loosely packed /
high-porosity soil holds more air and saturates+fails faster; steep slopes,
low vegetation root-binding, high ground vibration and a history of past
slides all raise landslide likelihood. These factors are combined with noise
into a continuous risk_score (0-100) and a binary landslide_occurred label.

Run:  python generate_dataset.py
Output: ../data/ner_landslide_sensor_dataset.xlsx  (Sensor_Readings sheet)
        and a Nodes sheet describing the monitored locations.
"""

import numpy as np
import pandas as pd
from datetime import datetime, timedelta

RNG_SEED = 42
N_ROWS = 4000
OUT_PATH = "ner_landslide_sensor_dataset.xlsx"

rng = np.random.default_rng(RNG_SEED)

# Monitored sensor nodes across NER (district town / highway corridor points).
# porosity_base / slope_base / veg_base give each node a "personality" so the
# dataset has realistic per-location clustering, not pure iid noise.
NODES = [
    # node_id, district, state, lat, lon, slope_base(deg), porosity_base(0-1), veg_base(%), elevation_m, hist_incidents
    ("NER-A01", "Kamrup Metro",   "Assam",             26.1445, 91.7362, 8,  0.28, 55, 55,   1),
    ("NER-A02", "Ri-Bhoi",        "Meghalaya",          25.9,   91.88,   22, 0.42, 40, 900,  3),
    ("NER-A03", "East Khasi Hills","Meghalaya",         25.5788, 91.8933, 35, 0.52, 30, 1500, 6),
    ("NER-A04", "West Jaintia Hills","Meghalaya",       25.43,  92.35,   30, 0.48, 35, 1200, 4),
    ("NER-A05", "Sonitpur",       "Assam",              26.63,  92.80,   12, 0.30, 50, 120,  1),
    ("NER-A06", "Papum Pare",     "Arunachal Pradesh",  27.10,  93.62,   38, 0.55, 45, 550,  5),
    ("NER-A07", "West Kameng",    "Arunachal Pradesh",  27.29,  92.40,   42, 0.58, 38, 2000, 7),
    ("NER-A08", "Dima Hasao",     "Assam",              25.48,  93.02,   33, 0.50, 42, 1100, 5),
    ("NER-A09", "Cachar",         "Assam",              24.83,  92.78,   14, 0.32, 48, 100,  2),
    ("NER-A10", "Aizawl",         "Mizoram",            23.7271,92.7176, 40, 0.60, 33, 1130, 8),
    ("NER-A11", "Kohima",         "Nagaland",           25.6751,94.1086, 37, 0.53, 37, 1444, 6),
    ("NER-A12", "Dimapur",        "Nagaland",           25.9091,93.7267, 15, 0.31, 46, 260,  2),
    ("NER-A13", "Imphal West",    "Manipur",            24.8170,93.9368, 20, 0.38, 41, 790,  3),
    ("NER-A14", "West Tripura",   "Tripura",            23.8315,91.2868, 18, 0.35, 44, 60,   2),
    ("NER-A15", "East Sikkim",    "Sikkim",             27.3389,88.6065, 44, 0.57, 32, 1650, 7),
    ("NER-A16", "North Sikkim",   "Sikkim",             27.72,  88.57,   48, 0.62, 25, 2100, 9),
    ("NER-A17", "Lower Subansiri","Arunachal Pradesh",  27.10,  93.83,   36, 0.51, 40, 800,  4),
    ("NER-A18", "Karbi Anglong",  "Assam",              25.85,  93.44,   26, 0.40, 43, 650,  3),
]

SOIL_TYPES = ["Rocky/Compact", "Loamy", "Sandy-Loam", "Clayey", "Loose Debris/Scree"]
# porosity multiplier per soil type -- loosely packed / debris soils hold more air & fail faster when saturated
SOIL_POROSITY_FACTOR = {
    "Rocky/Compact": 0.55,
    "Loamy": 0.85,
    "Sandy-Loam": 1.0,
    "Clayey": 0.9,
    "Loose Debris/Scree": 1.35,
}

def clip(x, lo=0, hi=100):
    return np.clip(x, lo, hi)

rows = []
start_date = datetime(2024, 1, 1)

for i in range(N_ROWS):
    node = NODES[rng.integers(0, len(NODES))]
    node_id, district, state, lat, lon, slope_base, porosity_base, veg_base, elev, hist = node

    # jitter the sensor's exact position slightly (simulates multiple sensors per district)
    jlat = lat + rng.normal(0, 0.03)
    jlon = lon + rng.normal(0, 0.03)

    ts = start_date + timedelta(hours=int(rng.integers(0, 24 * 540)))  # ~18 months of readings
    month = ts.month
    monsoon = 1.0 if month in (6, 7, 8, 9) else (0.5 if month in (5, 10) else 0.15)

    # weather (OpenWeatherMap-style fields)
    rainfall_24h = max(0, rng.gamma(shape=2.0, scale=18 * monsoon))
    rainfall_72h = rainfall_24h + max(0, rng.gamma(shape=2.2, scale=22 * monsoon))
    days_since_rain = 0 if rainfall_24h > 2 else int(rng.integers(0, 12))
    temperature_c = rng.normal(24 - elev / 250, 3)
    humidity_pct = clip(rng.normal(65 + 20 * monsoon, 8))

    # soil / ground sensors
    soil_type = rng.choice(SOIL_TYPES, p=[0.18, 0.24, 0.22, 0.18, 0.18])
    porosity_factor = SOIL_POROSITY_FACTOR[soil_type]
    soil_porosity_index = clip(porosity_base * porosity_factor + rng.normal(0, 0.05), 0, 1.5) / 1.5 * 100

    # soil moisture rises with rainfall and porosity (porous soil absorbs+saturates faster)
    soil_moisture_pct = clip(
        18 + rainfall_72h * 0.9 * (0.6 + porosity_factor * 0.5) - days_since_rain * 2.5 + rng.normal(0, 4)
    )

    # ground vibration/motion sensor (creep/micro-movement, unit: mm/s equivalent 0-10)
    vibration_intensity = clip(
        0.6 + (soil_moisture_pct / 100) * 3.5 + (slope_base / 50) * 2.5 + rng.normal(0, 0.6), 0, 10
    )

    slope_angle_deg = clip(slope_base + rng.normal(0, 3), 0, 70)
    vegetation_cover_pct = clip(veg_base + rng.normal(0, 6))
    distance_to_stream_km = max(0.05, rng.gamma(2.0, 1.2))
    historical_landslide_count = max(0, int(rng.poisson(hist * 0.5)))

    # ---- composite risk score (0-100), the target our ML model learns to reconstruct ----
    risk = (
        0.27 * (rainfall_72h / 150 * 100)
        + 0.22 * soil_moisture_pct
        + 0.14 * (vibration_intensity / 10 * 100)
        + 0.13 * (slope_angle_deg / 70 * 100)
        + 0.10 * soil_porosity_index
        + 0.08 * (historical_landslide_count / 10 * 100)
        + 0.06 * (max(0.0, 10 - distance_to_stream_km) / 10 * 100)
        - 0.10 * vegetation_cover_pct
    )
    risk_score = clip(risk + rng.normal(0, 6))

    # binary label: probabilistic threshold around the score (keeps it learnable, not trivial)
    prob_slide = 1 / (1 + np.exp(-(risk_score - 68) / 6))
    landslide_occurred = int(rng.random() < prob_slide)

    rows.append({
        "record_id": f"REC{i+1:05d}",
        "sensor_node_id": node_id,
        "district": district,
        "state": state,
        "latitude": round(jlat, 5),
        "longitude": round(jlon, 5),
        "timestamp": ts.strftime("%Y-%m-%d %H:%M"),
        "rainfall_mm_last_24h": round(rainfall_24h, 1),
        "rainfall_mm_last_72h": round(rainfall_72h, 1),
        "days_since_last_rainfall": days_since_rain,
        "temperature_c": round(temperature_c, 1),
        "humidity_pct": round(humidity_pct, 1),
        "soil_type": soil_type,
        "soil_moisture_pct": round(soil_moisture_pct, 1),
        "soil_porosity_index": round(soil_porosity_index, 1),
        "vibration_intensity": round(vibration_intensity, 2),
        "slope_angle_deg": round(slope_angle_deg, 1),
        "vegetation_cover_pct": round(vegetation_cover_pct, 1),
        "distance_to_stream_km": round(distance_to_stream_km, 2),
        "historical_landslide_count": historical_landslide_count,
        "elevation_m": elev,
        "risk_score": round(risk_score, 1),
        "landslide_occurred": landslide_occurred,
    })

df = pd.DataFrame(rows)

nodes_df = pd.DataFrame(NODES, columns=[
    "node_id", "district", "state", "latitude", "longitude",
    "slope_base_deg", "porosity_base", "vegetation_base_pct", "elevation_m", "historical_incidents_base"
])

with pd.ExcelWriter(OUT_PATH, engine="openpyxl") as writer:
    df.to_excel(writer, sheet_name="Sensor_Readings", index=False)
    nodes_df.to_excel(writer, sheet_name="Monitoring_Nodes", index=False)

print(f"Wrote {len(df)} rows -> {OUT_PATH}")
print(f"Positive rate (landslide_occurred=1): {df['landslide_occurred'].mean():.2%}")
print(df[["risk_score", "landslide_occurred"]].describe())
