import { useI18n } from "../i18n/I18nContext";

const CATEGORY_TONE = { LOW: "good", MODERATE: "warning", HIGH: "serious", SEVERE: "critical" };
const CATEGORY_ICON = { LOW: "✓", MODERATE: "⚠", HIGH: "⚠", SEVERE: "✖" };

export function Badge({ children, tone }) {
  const { t } = useI18n();
  const label = typeof children === "string" && t(`status.${children}`) !== `status.${children}`
    ? t(`status.${children}`)
    : children;
  return <span className={`badge ${tone}`}>{label}</span>;
}

export function CategoryBadge({ category }) {
  const { t } = useI18n();
  return (
    <span className={`badge ${category}`}>
      {CATEGORY_ICON[category] || ""} {t(`status.${category}`)}
    </span>
  );
}

export function categoryTone(category) {
  return CATEGORY_TONE[category] || "good";
}

export function StatTile({ label, value, tone }) {
  return (
    <div className="stat-tile">
      <div className="stat-tile__label">{label}</div>
      <div className={`stat-tile__value ${tone || ""}`}>{value}</div>
    </div>
  );
}

export function useTimeAgo() {
  const { t } = useI18n();
  return (iso) => {
    if (!iso) return "";
    const diff = (Date.now() - new Date(iso).getTime()) / 1000;
    if (diff < 60) return t("time.secondsAgo", { n: Math.max(0, Math.floor(diff)) });
    if (diff < 3600) return t("time.minutesAgo", { n: Math.floor(diff / 60) });
    if (diff < 86400) return t("time.hoursAgo", { n: Math.floor(diff / 3600) });
    return t("time.daysAgo", { n: Math.floor(diff / 86400) });
  };
}
