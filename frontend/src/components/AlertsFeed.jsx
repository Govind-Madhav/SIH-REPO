import { CategoryBadge, useTimeAgo } from "./common";
import { useI18n } from "../i18n/I18nContext";

export default function AlertsFeed({ alerts }) {
  const { t } = useI18n();
  const timeAgo = useTimeAgo();

  return (
    <div>
      <h1 className="page-title">{t("alerts.title")}</h1>
      <p className="page-subtitle">{t("alerts.subtitle")}</p>

      <div className="panel">
        {(!alerts || alerts.length === 0) && <div className="hint">{t("alerts.noAlerts")}</div>}
        {(alerts || []).map((a) => (
          <div className="alert-item" key={a.id}>
            <div className="alert-item__icon" style={{ background: "rgba(236,131,90,0.15)" }}>
              {a.type === "GROUND_REPORT" ? "📍" : "🤖"}
            </div>
            <div className="alert-item__body">
              <div className="alert-item__title">
                {a.node_name} ({a.district}) · <CategoryBadge category={a.category} />
                {" "}
                <span className="hint">{a.type === "GROUND_REPORT" ? t("alerts.humanReport") : t("alerts.aiPrediction")}</span>
              </div>
              <div className="alert-item__msg">{a.message}</div>
              <div className="alert-item__time">{timeAgo(a.created_at)}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
