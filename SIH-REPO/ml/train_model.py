"""
Trains the landslide risk-prediction models used by the backend's ML service.

Two models are trained on the same feature set (from data/ner_landslide_sensor_dataset.xlsx):
  1. risk_regressor      -> predicts a continuous risk_score (0-100)
  2. occurrence_classifier -> predicts P(landslide_occurred) for alert thresholding

Both are bundled with the fitted soil-type encoder and the exact feature
order into a single artifact: ml/model_bundle.joblib

Run:  python train_model.py
"""

import json
import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestRegressor, RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, r2_score, roc_auc_score, classification_report
from sklearn.preprocessing import LabelEncoder

DATA_PATH = "../data/ner_landslide_sensor_dataset.xlsx"
BUNDLE_PATH = "model_bundle.joblib"
METRICS_PATH = "training_metrics.json"

NUMERIC_FEATURES = [
    "rainfall_mm_last_24h",
    "rainfall_mm_last_72h",
    "days_since_last_rainfall",
    "temperature_c",
    "humidity_pct",
    "soil_moisture_pct",
    "soil_porosity_index",
    "vibration_intensity",
    "slope_angle_deg",
    "vegetation_cover_pct",
    "distance_to_stream_km",
    "historical_landslide_count",
    "elevation_m",
]
CATEGORICAL_FEATURES = ["soil_type"]
FEATURES = NUMERIC_FEATURES + ["soil_type_encoded"]

print("Loading dataset...")
df = pd.read_excel(DATA_PATH, sheet_name="Sensor_Readings")

soil_encoder = LabelEncoder()
df["soil_type_encoded"] = soil_encoder.fit_transform(df["soil_type"])

X = df[FEATURES]
y_risk = df["risk_score"]
y_occ = df["landslide_occurred"]

X_train, X_test, yr_train, yr_test, yo_train, yo_test = train_test_split(
    X, y_risk, y_occ, test_size=0.2, random_state=42, stratify=y_occ
)

print("Training risk regressor (RandomForestRegressor)...")
regressor = RandomForestRegressor(
    n_estimators=300, max_depth=12, min_samples_leaf=3, random_state=42, n_jobs=-1
)
regressor.fit(X_train, yr_train)
pred_risk = regressor.predict(X_test)
mae = mean_absolute_error(yr_test, pred_risk)
r2 = r2_score(yr_test, pred_risk)
print(f"  MAE={mae:.2f}  R2={r2:.3f}")

print("Training occurrence classifier (RandomForestClassifier)...")
classifier = RandomForestClassifier(
    n_estimators=300, max_depth=10, min_samples_leaf=3, random_state=42,
    class_weight="balanced_subsample", n_jobs=-1
)
classifier.fit(X_train, yo_train)
pred_proba = classifier.predict_proba(X_test)[:, 1]
pred_class = classifier.predict(X_test)
auc = roc_auc_score(yo_test, pred_proba)
report = classification_report(yo_test, pred_class, output_dict=True)
print(f"  ROC-AUC={auc:.3f}")
print(classification_report(yo_test, pred_class))

feature_importance = dict(
    sorted(zip(FEATURES, regressor.feature_importances_), key=lambda kv: -kv[1])
)
print("\nTop risk factors (feature importance):")
for k, v in list(feature_importance.items())[:6]:
    print(f"  {k:28s} {v:.3f}")

bundle = {
    "regressor": regressor,
    "classifier": classifier,
    "soil_encoder": soil_encoder,
    "numeric_features": NUMERIC_FEATURES,
    "categorical_features": CATEGORICAL_FEATURES,
    "feature_order": FEATURES,
    "soil_type_classes": list(soil_encoder.classes_),
}
joblib.dump(bundle, BUNDLE_PATH)
print(f"\nSaved model bundle -> {BUNDLE_PATH}")

metrics = {
    "regressor_mae": mae,
    "regressor_r2": r2,
    "classifier_roc_auc": auc,
    "classifier_report": report,
    "feature_importance": {k: float(v) for k, v in feature_importance.items()},
    "n_train": len(X_train),
    "n_test": len(X_test),
}
with open(METRICS_PATH, "w") as f:
    json.dump(metrics, f, indent=2)
print(f"Saved metrics -> {METRICS_PATH}")
