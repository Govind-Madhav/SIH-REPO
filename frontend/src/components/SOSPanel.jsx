import { useState } from "react";
import { api } from "../api";
import { Badge, useTimeAgo } from "./common";
import { useI18n } from "../i18n/I18nContext";

export default function SOSPanel({ vehicles, sosEvents, onResolved }) {
  const { t } = useI18n();
  const timeAgo = useTimeAgo();
  const ISSUE_TYPES = [
    { value: "vehicle_breakdown", label: t("sos.issueVehicleBreakdown") },
    { value: "medical", label: t("sos.issueMedical") },
    { value: "road_blocked", label: t("sos.issueRoadBlocked") },
    { value: "accident", label: t("sos.issueAccident") },
    { value: "other", label: t("sos.issueOther") },
  ];

  const [driverName, setDriverName] = useState("");
  const [phone, setPhone] = useState("");
  const [vehicleId, setVehicleId] = useState("");
  const [issueType, setIssueType] = useState("vehicle_breakdown");
  const [message, setMessage] = useState("");
  const [coords, setCoords] = useState(null);
  const [sending, setSending] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  function grabLocation() {
    if (!navigator.geolocation) {
      setError(t("sos.errorGeoUnavailable"));
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => setCoords({ lat: pos.coords.latitude, lon: pos.coords.longitude }),
      () => setError(t("sos.errorGeoFailed")),
      { enableHighAccuracy: true, timeout: 8000 }
    );
  }

  async function sendSOS() {
    if (!driverName || !phone) {
      setError(t("sos.errorNamePhone"));
      return;
    }
    setSending(true);
    setError(null);
    try {
      const loc = coords || { lat: 25.6, lon: 92.9 };
      const res = await api.sendSOS({
        vehicle_id: vehicleId || null,
        driver_name: driverName,
        phone,
        lat: loc.lat,
        lon: loc.lon,
        issue_type: issueType,
        message,
      });
      setResult(res);
    } catch {
      setError(t("sos.errorOffline"));
    } finally {
      setSending(false);
    }
  }

  return (
    <div>
      <h1 className="page-title">{t("sos.title")}</h1>
      <p className="page-subtitle">{t("sos.subtitle")}</p>

      <div className="two-col">
        <div className="panel">
          <div className="sos-hero">
            <button className="sos-button" onClick={sendSOS} disabled={sending}>
              {sending ? t("sos.sendingButton") : t("sos.sendButton")}
            </button>
            <button className="btn secondary" onClick={grabLocation}>{t("sos.useGps")}</button>
            {coords && <div className="hint">{t("sos.locationCaptured", { lat: coords.lat.toFixed(4), lon: coords.lon.toFixed(4) })}</div>}
          </div>

          <div className="form-grid">
            <div className="field">
              <label>{t("sos.nameLabel")}</label>
              <input value={driverName} onChange={(e) => setDriverName(e.target.value)} placeholder={t("sos.namePlaceholder")} />
            </div>
            <div className="field">
              <label>{t("sos.phoneLabel")}</label>
              <input value={phone} onChange={(e) => setPhone(e.target.value)} placeholder={t("sos.phonePlaceholder")} />
            </div>
            <div className="field">
              <label>{t("sos.vehicleLabel")}</label>
              <select value={vehicleId} onChange={(e) => setVehicleId(e.target.value)}>
                <option value="">{t("sos.vehicleNone")}</option>
                {vehicles.map((v) => (
                  <option key={v.id} value={v.id}>{v.id} · {v.cargo_type} ({v.driver_name})</option>
                ))}
              </select>
            </div>
            <div className="field">
              <label>{t("sos.issueTypeLabel")}</label>
              <select value={issueType} onChange={(e) => setIssueType(e.target.value)}>
                {ISSUE_TYPES.map((ty) => <option key={ty.value} value={ty.value}>{ty.label}</option>)}
              </select>
            </div>
            <div className="field" style={{ gridColumn: "1 / -1" }}>
              <label>{t("sos.messageLabel")}</label>
              <textarea value={message} onChange={(e) => setMessage(e.target.value)} placeholder={t("sos.messagePlaceholder")} />
            </div>
          </div>

          {error && <div className="banner warn" style={{ marginTop: 12 }}>{error}</div>}
          {result && (
            <div className="banner ok" style={{ marginTop: 12 }}>
              {t("sos.successMessage", { id: result.event.id, count: result.notifications.length })}
            </div>
          )}
        </div>

        <div className="panel">
          <div className="panel__title">{t("sos.openEventsTitle")}</div>
          {(!sosEvents || sosEvents.length === 0) && <div className="hint">{t("sos.noEvents")}</div>}
          {(sosEvents || []).map((e) => (
            <div className="alert-item" key={e.id}>
              <div className="alert-item__icon" style={{ background: "rgba(211,59,59,0.15)" }}>🚨</div>
              <div className="alert-item__body">
                <div className="alert-item__title">
                  {e.driver_name} · <Badge tone={e.status}>{e.status}</Badge>
                </div>
                <div className="alert-item__msg">{e.issue_type.replace(/_/g, " ")} — {e.message || t("sos.noDetails")}</div>
                <div className="alert-item__time">{timeAgo(e.created_at)}</div>
                {e.status === "OPEN" && (
                  <button className="btn secondary" style={{ marginTop: 6 }} onClick={() => onResolved(e.id)}>
                    {t("sos.markResolved")}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
