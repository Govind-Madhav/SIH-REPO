import { StatTile, CategoryBadge, useTimeAgo } from "./common";
import { useI18n } from "../i18n/I18nContext";

export default function Dashboard({ summary, alerts }) {
  const { t } = useI18n();
  const timeAgo = useTimeAgo();

  if (!summary) return <div className="hint">{t("dashboard.loading")}</div>;

  const cat = summary.nodes_by_category || {};

  return (
    <div>
      <h1 className="page-title">{t("dashboard.title")}</h1>
      <p className="page-subtitle">{t("dashboard.subtitle", { count: summary.total_nodes })}</p>

      <div className="stat-grid">
        <StatTile label={t("dashboard.statMonitoredNodes")} value={summary.total_nodes} />
        <StatTile label={t("dashboard.statLowRisk")} value={cat.LOW || 0} tone="good" />
        <StatTile label={t("dashboard.statModerateRisk")} value={cat.MODERATE || 0} tone="warning" />
        <StatTile label={t("dashboard.statHighRisk")} value={cat.HIGH || 0} tone="serious" />
        <StatTile label={t("dashboard.statSevereRisk")} value={cat.SEVERE || 0} tone="critical" />
        <StatTile label={t("dashboard.statFlaggedCorridors")} value={summary.flagged_corridors} tone="warning" />
        <StatTile label={t("dashboard.statBlockedCorridors")} value={summary.blocked_corridors} tone="critical" />
        <StatTile label={t("dashboard.statActiveAlerts")} value={summary.active_alerts} tone={summary.active_alerts ? "critical" : "good"} />
        <StatTile label={t("dashboard.statOpenSOS")} value={summary.open_sos} tone={summary.open_sos ? "critical" : "good"} />
        <StatTile label={t("dashboard.statVehiclesTracked")} value={summary.vehicles_total} />
      </div>

      <div className="two-col">
        <div className="panel">
          <div className="panel__title">{t("dashboard.districtStatusTitle")}</div>
          <div style={{ maxHeight: 360, overflowY: "auto" }}>
            <table className="table">
              <thead>
                <tr>
                  <th>{t("dashboard.colDistrict")}</th>
                  <th>{t("dashboard.colState")}</th>
                  <th>{t("dashboard.colRiskScore")}</th>
                  <th>{t("dashboard.colStatus")}</th>
                </tr>
              </thead>
              <tbody>
                {(summary.district_status || []).map((d) => (
                  <tr key={d.node_name}>
                    <td>{d.district}{d.storm_event ? " ⛈" : ""}{d.manually_flagged ? " 🚩" : ""}</td>
                    <td>{d.state}</td>
                    <td>{d.risk_score}</td>
                    <td><CategoryBadge category={d.category} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="panel">
          <div className="panel__title">{t("dashboard.recentAlertsTitle")}</div>
          {(!alerts || alerts.length === 0) && <div className="hint">{t("dashboard.noAlerts")}</div>}
          {(alerts || []).slice(0, 12).map((a) => (
            <div className="alert-item" key={a.id}>
              <div className="alert-item__icon" style={{ background: "rgba(236,131,90,0.15)" }}>
                {a.type === "GROUND_REPORT" ? "📍" : "🤖"}
              </div>
              <div className="alert-item__body">
                <div className="alert-item__title">
                  {a.node_name} · <CategoryBadge category={a.category} />
                </div>
                <div className="alert-item__msg">{a.message}</div>
                <div className="alert-item__time">{timeAgo(a.created_at)}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
