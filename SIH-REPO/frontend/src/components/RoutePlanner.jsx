import { useEffect, useState } from "react";
import { api } from "../api";
import { Badge } from "./common";
import MapView from "./MapView";
import { useI18n } from "../i18n/I18nContext";

export default function RoutePlanner({ nodes, edges, vehicles }) {
  const { t } = useI18n();
  const [origin, setOrigin] = useState("GHY");
  const [destination, setDestination] = useState("ITN");
  const [plan, setPlan] = useState(null);
  const [selected, setSelected] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  async function runPlan() {
    if (origin === destination) {
      setError(t("routes.errorSameNode"));
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const result = await api.planRoute(origin, destination, 3);
      setPlan(result);
      setSelected(0);
    } catch (e) {
      setError(e?.response?.data?.detail || t("routes.errorGeneric"));
      setPlan(null);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    runPlan(); // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const activeRoute = plan?.routes?.[selected];
  const routeLabel = (label) => (label === "Recommended" ? t("routes.labelRecommended") : t("routes.labelAlternate"));

  return (
    <div>
      <h1 className="page-title">{t("routes.title")}</h1>
      <p className="page-subtitle">{t("routes.subtitle")}</p>

      <div className="panel">
        <div className="form-grid" style={{ gridTemplateColumns: "1fr 1fr auto", alignItems: "end" }}>
          <div className="field">
            <label>{t("routes.origin")}</label>
            <select value={origin} onChange={(e) => setOrigin(e.target.value)}>
              {nodes.map((n) => (
                <option key={n.key} value={n.key}>{n.name} ({n.district})</option>
              ))}
            </select>
          </div>
          <div className="field">
            <label>{t("routes.destination")}</label>
            <select value={destination} onChange={(e) => setDestination(e.target.value)}>
              {nodes.map((n) => (
                <option key={n.key} value={n.key}>{n.name} ({n.district})</option>
              ))}
            </select>
          </div>
          <button className="btn" onClick={runPlan} disabled={loading}>
            {loading ? t("routes.planningButton") : t("routes.planButton")}
          </button>
        </div>
        {error && <div className="banner warn" style={{ marginTop: 12 }}>{error}</div>}
      </div>

      <div className="two-col">
        <MapView
          nodes={nodes}
          edges={edges}
          vehicles={vehicles}
          reports={[]}
          highlightedPath={activeRoute?.path}
        />

        <div>
          {plan?.routes?.map((r, i) => (
            <div
              key={r.route_id}
              className={`route-card ${i === selected ? "selected" : ""}`}
              onClick={() => setSelected(i)}
            >
              <div className="route-card__head">
                <span className="route-card__title">{routeLabel(r.label)}: {r.path_names.join(" → ")}</span>
                <Badge tone={r.status}>{r.status}</Badge>
              </div>
              <div className="route-card__meta">
                <span>📏 {r.total_distance_km} {t("common.km")}</span>
                <span>⏱ {r.estimated_time_hr} {t("common.hoursShort")}</span>
                <span>⚠ {t("routes.maxRisk")} {r.max_segment_risk}</span>
              </div>
              {r.segments.map((s, si) => (
                <div className={`segment-row ${s.blocked ? "blocked" : s.flagged ? "flagged" : ""}`} key={si}>
                  <span className="dot" />
                  {s.from_name} → {s.to_name} ({s.highway_ref}) — {t("dashboard.colRiskScore")} {s.risk_score} ({t(`status.${s.category}`)})
                  {s.blocked && ` · ${t("routes.autoAvoided")}`}
                  {!s.blocked && s.flagged && ` · ${t("routes.caution")}`}
                </div>
              ))}
            </div>
          ))}
          {plan?.routes?.length === 0 && <div className="hint">{t("routes.noRoutes")}</div>}
        </div>
      </div>
    </div>
  );
}
