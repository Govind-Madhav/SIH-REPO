import { useState } from "react";
import { api } from "../api";
import { useOfflineQueue } from "../hooks/useOfflineQueue";
import { useTimeAgo } from "./common";
import { useI18n } from "../i18n/I18nContext";

function fileToDataUrl(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}

function dataUrlToBlob(dataUrl) {
  const [meta, b64] = dataUrl.split(",");
  const mime = meta.match(/:(.*?);/)[1];
  const bytes = atob(b64);
  const arr = new Uint8Array(bytes.length);
  for (let i = 0; i < bytes.length; i++) arr[i] = bytes.charCodeAt(i);
  return new Blob([arr], { type: mime });
}

async function submitQueuedReport(item) {
  const fd = new FormData();
  fd.append("reporter_name", item.reporter_name);
  fd.append("phone", item.phone || "");
  fd.append("incident_type", item.incident_type);
  fd.append("description", item.description || "");
  fd.append("lat", item.lat);
  fd.append("lon", item.lon);
  if (item.photoDataUrl) fd.append("photo", dataUrlToBlob(item.photoDataUrl), "photo.jpg");
  return api.submitReport(fd);
}

export default function ReportForm({ reports, onSubmitted }) {
  const { t } = useI18n();
  const timeAgo = useTimeAgo();

  const INCIDENT_TYPES = [
    { value: "landslide_occurred", label: t("report.incidentLandslideOccurred") },
    { value: "road_blocked", label: t("report.incidentRoadBlocked") },
    { value: "bridge_damage", label: t("report.incidentBridgeDamage") },
    { value: "flooding", label: t("report.incidentFlooding") },
    { value: "heavy_rain_warning", label: t("report.incidentHeavyRainWarning") },
    { value: "minor_obstruction", label: t("report.incidentMinorObstruction") },
    { value: "all_clear", label: t("report.incidentAllClear") },
  ];
  const incidentLabel = (value) => INCIDENT_TYPES.find((i) => i.value === value)?.label || value.replace(/_/g, " ");

  const [reporterName, setReporterName] = useState("");
  const [phone, setPhone] = useState("");
  const [incidentType, setIncidentType] = useState("road_blocked");
  const [description, setDescription] = useState("");
  const [coords, setCoords] = useState(null);
  const [photoFile, setPhotoFile] = useState(null);
  const [photoPreview, setPhotoPreview] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [status, setStatus] = useState(null);

  const { queue, online, enqueue } = useOfflineQueue(submitQueuedReport);

  function grabLocation() {
    if (!navigator.geolocation) return;
    navigator.geolocation.getCurrentPosition(
      (pos) => setCoords({ lat: pos.coords.latitude, lon: pos.coords.longitude }),
      () => setStatus({ type: "warn", text: t("report.errorGeoFailed") })
    );
  }

  function onPhotoChange(e) {
    const file = e.target.files?.[0];
    setPhotoFile(file || null);
    setPhotoPreview(file ? URL.createObjectURL(file) : null);
  }

  async function submit() {
    if (!reporterName || !coords) {
      setStatus({ type: "warn", text: t("report.errorRequired") });
      return;
    }
    setSubmitting(true);
    setStatus(null);
    const base = {
      reporter_name: reporterName,
      phone,
      incident_type: incidentType,
      description,
      lat: coords.lat,
      lon: coords.lon,
    };

    try {
      if (!navigator.onLine) throw new Error("offline");
      const fd = new FormData();
      Object.entries(base).forEach(([k, v]) => fd.append(k, v));
      if (photoFile) fd.append("photo", photoFile);
      await api.submitReport(fd);
      setStatus({ type: "ok", text: t("report.successOnline") });
      onSubmitted?.();
    } catch {
      const photoDataUrl = photoFile ? await fileToDataUrl(photoFile) : null;
      enqueue({ ...base, photoDataUrl });
      setStatus({ type: "warn", text: t("report.successOffline") });
    } finally {
      setSubmitting(false);
      setReporterName("");
      setDescription("");
      setPhotoFile(null);
      setPhotoPreview(null);
    }
  }

  return (
    <div>
      <h1 className="page-title">{t("report.title")}</h1>
      <p className="page-subtitle">{t("report.subtitle")}</p>

      {!online && (
        <div className="banner warn">{t("report.offlineBanner", { count: queue.length })}</div>
      )}
      {online && queue.length > 0 && (
        <div className="banner warn">{t("report.syncingBanner", { count: queue.length })}</div>
      )}

      <div className="two-col">
        <div className="panel">
          <div className="form-grid">
            <div className="field">
              <label>{t("report.nameLabel")}</label>
              <input value={reporterName} onChange={(e) => setReporterName(e.target.value)} placeholder={t("report.namePlaceholder")} />
            </div>
            <div className="field">
              <label>{t("report.phoneLabel")}</label>
              <input value={phone} onChange={(e) => setPhone(e.target.value)} placeholder={t("report.phonePlaceholder")} />
            </div>
            <div className="field" style={{ gridColumn: "1 / -1" }}>
              <label>{t("report.incidentTypeLabel")}</label>
              <select value={incidentType} onChange={(e) => setIncidentType(e.target.value)}>
                {INCIDENT_TYPES.map((ty) => <option key={ty.value} value={ty.value}>{ty.label}</option>)}
              </select>
            </div>
            <div className="field" style={{ gridColumn: "1 / -1" }}>
              <label>{t("report.descriptionLabel")}</label>
              <textarea value={description} onChange={(e) => setDescription(e.target.value)} placeholder={t("report.descriptionPlaceholder")} />
            </div>
            <div className="field">
              <label>{t("report.locationLabel")}</label>
              <button className="btn secondary" onClick={grabLocation} type="button">{t("report.useGps")}</button>
              {coords && <span className="hint">{coords.lat.toFixed(4)}, {coords.lon.toFixed(4)}</span>}
            </div>
            <div className="field">
              <label>{t("report.photoLabel")}</label>
              <input type="file" accept="image/*" capture="environment" onChange={onPhotoChange} />
              {photoPreview && <img src={photoPreview} alt="preview" className="photo-preview" />}
            </div>
          </div>

          <button className="btn full" style={{ marginTop: 14 }} onClick={submit} disabled={submitting}>
            {submitting ? t("report.submittingButton") : t("report.submitButton")}
          </button>
          {status && <div className={`banner ${status.type === "ok" ? "ok" : "warn"}`} style={{ marginTop: 12 }}>{status.text}</div>}
        </div>

        <div className="panel">
          <div className="panel__title">{t("report.recentReportsTitle")}</div>
          {(!reports || reports.length === 0) && <div className="hint">{t("report.noReports")}</div>}
          {(reports || []).slice(0, 15).map((r) => (
            <div className="alert-item" key={r.id}>
              <div className="alert-item__icon" style={{ background: "rgba(236,131,90,0.15)" }}>📍</div>
              <div className="alert-item__body">
                <div className="alert-item__title">
                  {t("report.nearTemplate", { incident: incidentLabel(r.incident_type), node: r.nearest_node_name })}
                </div>
                <div className="alert-item__msg">{r.description}</div>
                <div className="alert-item__time">{t("report.reportedBy")} {r.reporter_name} · {timeAgo(r.created_at)}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
