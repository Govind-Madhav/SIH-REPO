import { useTheme } from "../theme/ThemeContext";
import { useI18n } from "../i18n/I18nContext";

const OPTIONS = [
  { value: "light", icon: "☀️" },
  { value: "dark", icon: "🌙" },
  { value: "system", icon: "🖥️" },
];

export default function ThemeToggle() {
  const { mode, setMode } = useTheme();
  const { t } = useI18n();
  const labelKey = { light: "themeLight", dark: "themeDark", system: "themeSystem" };

  return (
    <div className="theme-toggle" role="group" aria-label="Theme">
      {OPTIONS.map((opt) => (
        <button
          key={opt.value}
          type="button"
          className={`theme-toggle__btn ${mode === opt.value ? "active" : ""}`}
          onClick={() => setMode(opt.value)}
          title={t(`common.${labelKey[opt.value]}`)}
        >
          <span aria-hidden="true">{opt.icon}</span>
          <span className="theme-toggle__label">{t(`common.${labelKey[opt.value]}`)}</span>
        </button>
      ))}
    </div>
  );
}
