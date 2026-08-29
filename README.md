# NER LogiSense

**AI-Based Smart Logistics and Accessibility Intelligence Platform for the North Eastern Region**
Prototype built for SIH 2026 (Problem Statement: AI-Based Smart Logistics and Accessibility Intelligence Platform for NER).

Combines simulated ground sensors (soil moisture + vibration/motion), weather data, and an ML model
trained to fuse them into a landslide risk score, then feeds that score into an automatic route-rerouting
engine, a live GIS dashboard, GPS vehicle tracking, an SOS system, and WhatsApp/SMS alerting.

## The core idea, in one line

> Rain saturates loosely-packed/porous soil faster than compact soil; saturated soil + steep slope +
> rising ground vibration = landslide risk. We fuse those signals (rainfall, soil moisture, soil
> porosity/type, ground vibration, slope) through a trained ML model into a 0–100 risk score per
> monitored location, then let that score drive everything downstream: route flags, automatic
> rerouting, alerts, and notifications.

## Architecture

```
 ┌────────────────────┐      ┌─────────────────────────┐      ┌──────────────────────────┐
 │  data/               │      │  ml/                     │      │  backend/ (FastAPI)      │
 │  generate_dataset.py │ ───► │  train_model.py           │ ───► │  ml_service loads         │
 │  → dummy sensor +    │      │  → RandomForest regressor │      │  model_bundle.joblib      │
 │    weather Excel      │      │    (risk score 0-100)     │      │  and scores every live    │
 │    dataset (4000 rows)│      │  → RandomForest classifier│      │  sensor reading            │
 │                       │      │    (P(landslide))         │      │                            │
 └────────────────────┘      └─────────────────────────┘      └───────────┬──────────────┘
                                                                            │
                              ┌─────────────────────────────────────────────┼───────────────────────┐
                              │ simulation_service: simulates 18 NER sensor  │  routing_service:       │
                              │ nodes drifting over time (with random "storm │  NetworkX graph, live    │
                              │ events"), re-scores each via ml_service every│  risk-weighted Dijkstra/ │
                              │ few seconds, raises alerts on HIGH/SEVERE    │  k-shortest-paths ->     │
                              │                                              │  auto-avoids flagged/    │
                              │ vehicle_service: simulates GPS-tracked       │  blocked segments         │
                              │ logistics vehicles moving along the risk-    │                          │
                              │ aware planned route                          │  notify_service: Twilio  │
                              │                                              │  WhatsApp/SMS, graceful  │
                              │ reports_service: ground-truth incident       │  fallback to simulated    │
                              │ reports (with photo) manually override the   │  log + offline retry queue│
                              │ AI score for the nearest node                │                          │
                              └──────────────────────────────────────────────┴──────────────────────────┘
                                                            │
                                                   WebSocket + REST API
                                                            │
                              ┌─────────────────────────────▼──────────────────────────────┐
                              │  frontend/ (React + Vite + Leaflet)                          │
                              │  Dashboard · Live GIS Map · AI Route Planner · Emergency SOS │
                              │  · Field Incident Report (offline-queued) · Alerts Log       │
                              │  · WhatsApp/SMS Notification Log · EN/HI/AS language switch  │
                              └───────────────────────────────────────────────────────────────┘
```

## What's real vs simulated in this prototype

| Piece | Status |
|---|---|
| ML risk model (RandomForest regressor + classifier) | **Real**, trained on the generated dataset, MAE 5.0 / R² 0.85 / ROC-AUC 0.94 |
| Road network graph (18 NER towns, 20 corridors) | Real place names/coordinates, plausible distances; **not** survey-grade GIS |
| Sensor readings (moisture, vibration, rainfall) | **Simulated** — a background loop drifts values realistically and randomly injects "storm events"; swap for real MQTT/LoRaWAN sensor feed by replacing `simulation_service.py`'s tick logic |
| Weather | Uses **OpenWeatherMap** if you supply a key; otherwise the simulator's own rainfall model |
| Vehicle GPS tracking | **Simulated** — vehicles move along their AI-planned route at a set speed; swap for a real GPS device feed |
| WhatsApp / SMS | Uses **Twilio** if you supply credentials; otherwise every message is logged to an in-app "Outbox" (fully visible in the Notifications tab) so the whole pipeline is demoable with zero external accounts |
| Offline sync | Field reports queue in the browser's `localStorage` and flush automatically on reconnect — demonstrates the requirement without a full PWA/service-worker build |
| Multilingual | **Real** — every UI section (nav, dashboard, map legend, route planner, SOS, report form, alerts, notifications) is fully translated EN/HI/AS via `frontend/src/i18n`, with the choice persisted in the browser. Dynamically generated content (AI alert message bodies, district/state names) stays in English since that's backend-generated data, not UI chrome |
| Theme | **Real** — Light / Dark / System toggle in the top bar, persisted in the browser; "System" follows the OS's light/dark preference live |

## Repo layout

```
data/       generate_dataset.py           -> writes ner_landslide_sensor_dataset.xlsx
ml/         train_model.py                -> trains + saves model_bundle.joblib
backend/    FastAPI app (app/main.py)     -> REST + WebSocket API on :8000
frontend/   React + Vite app              -> UI on :5173 (theme + i18n in src/theme, src/i18n)
start.bat   one-click launcher for Windows -> installs if needed, starts both, opens the browser
```

## Quick start (Windows, one click)

Double-click **`start.bat`** at the project root. It will, in order:
1. Create the `.venv` virtual environment if missing, and install backend/ML dependencies.
2. Generate the sensor dataset and train the ML model if they don't exist yet.
3. `npm install` the frontend if `node_modules` is missing.
4. Launch the backend and frontend, each in its own terminal window (so you can watch their logs).
5. Wait for the frontend to come online, then **open it in your default browser automatically**.

To stop everything, just close the two terminal windows it opened ("NER LogiSense - Backend" /
"NER LogiSense - Frontend"). Re-running `start.bat` after the first time is fast — it skips any
step that's already done.

## Setup (manual / macOS / Linux)

### 1. Python environment (data + ML + backend)

```bash
python -m venv .venv
# Windows:
.venv\Scripts\activate
# macOS/Linux:
source .venv/bin/activate

pip install -r ml/requirements.txt
pip install -r backend/requirements.txt
```

### 2. Generate the dataset and train the model (already done once, re-run any time)

```bash
cd data && python generate_dataset.py && cd ..
cd ml && python train_model.py && cd ..
```

### 3. Configure API keys (optional — everything works with zero keys)

```bash
cd backend
copy .env.example .env      # Windows
# cp .env.example .env      # macOS/Linux
```

Fill in whichever of these you have — leave blank to stay in simulated mode:

- `OPENWEATHERMAP_API_KEY` — https://openweathermap.org/api (free tier is enough)
- `TWILIO_ACCOUNT_SID` / `TWILIO_AUTH_TOKEN` / `TWILIO_SMS_FROM_NUMBER` / `TWILIO_WHATSAPP_FROM_NUMBER` —
  https://www.twilio.com/try-twilio (free trial gives you a WhatsApp Sandbox + a trial SMS number)

### 4. Run the backend

```bash
cd backend
uvicorn app.main:app --reload --port 8000
```

API docs: http://localhost:8000/docs

### 5. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

App: http://localhost:5173 (already configured via `frontend/.env` to talk to `http://localhost:8000`)

## Demoing the automatic-rerouting story

1. Open **Route Planner**, plan Guwahati → Itanagar. You'll see 3 real alternate corridors
   (via Tezpur / via Tezpur+Bomdila / via Diphu+Ziro) each scored by live risk.
2. Open **Report Incident**, file a "Landslide already happened" report near Nongpoh (use
   "Use my GPS location" — or just let the browser prompt fail, since Nongpoh's coordinates are
   hard-set in the demo network). That corridor is instantly flagged SEVERE.
3. Go back to **Route Planner** and re-plan — the engine now routes around it automatically, and
   **Notifications** shows the WhatsApp/SMS alerts that went out to logistics dispatch and the
   district authority.
4. **Emergency SOS** simulates a driver's vehicle-breakdown button — dispatch is notified instantly
   over both channels.
5. **Live Map** shows all 18 sensor nodes color-coded by AI risk, moving vehicle markers, and any
   ground-reported incident pins, all updating in real time over WebSocket as the background
   simulation runs (it randomly injects "storm events" so risk visibly climbs and alerts fire on
   their own — no manual action needed to see the AI in motion).

## Retraining / extending the ML model

- `data/generate_dataset.py` — tune the synthetic generator (more nodes, different soil physics) and
  re-run to produce a new `ner_landslide_sensor_dataset.xlsx`.
- `ml/train_model.py` — swap in a different model (XGBoost, gradient boosting), retrain, and
  `backend/app/services/ml_service.py` will pick up the new `model_bundle.joblib` automatically as
  long as the bundle keys stay the same.
- To plug in real sensor hardware: replace the tick logic in
  `backend/app/services/simulation_service.py` with real reads (MQTT/LoRaWAN/HTTP callback) —
  everything downstream (routing, alerts, dashboard, notifications) already consumes `STATE` generically.

## Known prototype limitations (be upfront about these in the pitch)

- In-memory state (alerts/reports/SOS/outbox reset on backend restart) — fine for a demo, swap for
  Postgres/SQLite persistence for a production build.
- Road graph is a simplified 18-node network, not a full GIS road dataset — production would ingest
  actual NHAI/state PWD road geometry.
- Positive-class landslide events are rare in the synthetic dataset (~6%), which is realistic but means
  classifier recall (0.57) has room to improve with more/better-labeled data — the continuous risk-score
  regressor (R² 0.85) is the primary signal used everywhere in the app.
