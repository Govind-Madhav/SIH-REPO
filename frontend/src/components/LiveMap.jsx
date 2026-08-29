import MapView from "./MapView";
import { useI18n } from "../i18n/I18nContext";

const LEGEND_COLORS = ["#0ca30c", "#fab219", "#ec835a", "#d03b3b"];

export default function LiveMap({ nodes, edges, vehicles, reports }) {
  const { t } = useI18n();
  const legendLabels = [
    t("map.legendLow"),
    t("map.legendModerate"),
    t("map.legendHigh"),
    t("map.legendSevere"),
  ];

  return (
    <div>
      <h1 className="page-title">{t("map.title")}</h1>
      <p className="page-subtitle">{t("map.subtitle")}</p>
      <MapView nodes={nodes} edges={edges} vehicles={vehicles} reports={reports} height="72vh" />
      <div className="legend">
        {legendLabels.map((label, i) => (
          <div className="legend__item" key={label}>
            <span className="legend__swatch" style={{ background: LEGEND_COLORS[i] }} />
            {label}
          </div>
        ))}
        <div className="legend__item">{t("map.legendIcons")}</div>
      </div>
    </div>
  );
}
