import { MapContainer, TileLayer, CircleMarker, Polyline, Marker, Popup, Tooltip } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { API_BASE } from "../api";
import { useTheme } from "../theme/ThemeContext";
import { useI18n } from "../i18n/I18nContext";

const CATEGORY_COLOR = { LOW: "#0ca30c", MODERATE: "#fab219", HIGH: "#ec835a", SEVERE: "#d03b3b" };
const NER_CENTER = [25.6, 92.9];

function divIcon(emoji, bg) {
  return L.divIcon({
    className: "",
    html: `<div style="width:26px;height:26px;border-radius:50%;background:${bg};display:flex;align-items:center;justify-content:center;font-size:14px;border:2px solid rgba(255,255,255,0.7);box-shadow:0 1px 4px rgba(0,0,0,0.4)">${emoji}</div>`,
    iconSize: [26, 26],
    iconAnchor: [13, 13],
  });
}

const vehicleIcon = divIcon("🚚", "#3987e5");
const sosVehicleIcon = divIcon("🚨", "#d03b3b");
const reportIcon = divIcon("📍", "#ec835a");

export default function MapView({ nodes = [], edges = [], vehicles = [], reports = [], highlightedPath = null, height }) {
  const { effective } = useTheme();
  const { t } = useI18n();
  const nodeByKey = Object.fromEntries(nodes.map((n) => [n.node_key || n.key, n]));

  return (
    <div className="map-wrap" style={height ? { height } : undefined}>
      <MapContainer center={NER_CENTER} zoom={6} style={{ height: "100%", width: "100%" }} scrollWheelZoom>
        <TileLayer
          key={effective}
          className={effective === "dark" ? "dark-basemap" : ""}
          attribution='&copy; OpenStreetMap contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {edges.map((e, i) => {
          const a = nodeByKey[e.from];
          const b = nodeByKey[e.to];
          if (!a || !b) return null;
          const color = e.blocked ? "#d03b3b" : CATEGORY_COLOR[e.category] || "#3987e5";
          return (
            <Polyline
              key={`edge-${i}`}
              positions={[[a.lat, a.lon], [b.lat, b.lon]]}
              pathOptions={{
                color,
                weight: e.flagged ? 4 : 2.5,
                opacity: e.blocked ? 0.55 : 0.75,
                dashArray: e.blocked ? "6 6" : undefined,
              }}
            >
              <Tooltip sticky>
                {e.highway_ref}: {nodeByKey[e.from]?.name} ↔ {nodeByKey[e.to]?.name}
                <br />
                {t("dashboard.colRiskScore")} {e.risk_score} ({t(`status.${e.category}`)}){" "}
                {e.blocked ? `· ${t("status.AVOID").toUpperCase()}` : e.flagged ? `· ${t("routes.caution")}` : ""}
              </Tooltip>
            </Polyline>
          );
        })}

        {highlightedPath && highlightedPath.length > 1 && (
          <Polyline
            positions={highlightedPath.map((k) => [nodeByKey[k]?.lat, nodeByKey[k]?.lon]).filter(Boolean)}
            pathOptions={{ color: "#ffffff", weight: 3, opacity: 0.9, dashArray: "1 8" }}
          />
        )}

        {nodes.map((n) => {
          const key = n.node_key || n.key;
          const color = CATEGORY_COLOR[n.category] || "#0ca30c";
          return (
            <CircleMarker
              key={key}
              center={[n.lat, n.lon]}
              radius={n.storm_event ? 11 : 8}
              pathOptions={{ color: "#0d0d0d", weight: 1.5, fillColor: color, fillOpacity: 0.9 }}
            >
              <Popup>
                <b>{n.name}</b> ({n.district}, {n.state})
                <br />
                {t("dashboard.colRiskScore")}: {n.risk_score ?? "-"} · {t(`status.${n.category}`)}
                {n.storm_event ? <><br /><b>⛈ {t("map.activeStormEvent")}</b></> : null}
              </Popup>
              <Tooltip>{n.name} · {t(`status.${n.category}`)}</Tooltip>
            </CircleMarker>
          );
        })}

        {vehicles.map((v) => (
          <Marker key={v.id} position={[v.lat, v.lon]} icon={v.status === "SOS" ? sosVehicleIcon : vehicleIcon}>
            <Popup>
              <b>{v.id}</b> — {v.cargo_type}
              <br />
              {v.driver_name}
              <br />
              {v.origin_name} → {v.destination_name}
              <br />
              {t(`status.${v.status}`) !== `status.${v.status}` ? t(`status.${v.status}`) : v.status}
            </Popup>
          </Marker>
        ))}

        {reports.map((r) => (
          <Marker key={r.id} position={[r.lat, r.lon]} icon={reportIcon}>
            <Popup>
              <b>{r.incident_type.replace(/_/g, " ")}</b>
              <br />
              {r.description}
              <br />
              <i>{t("report.reportedBy")} {r.reporter_name}</i>
              {r.photo_path && (
                <div>
                  <img src={`${API_BASE}${r.photo_path}`} alt="report" style={{ maxWidth: 180, marginTop: 6, borderRadius: 6 }} />
                </div>
              )}
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
}
