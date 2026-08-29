import { useCallback, useEffect, useState } from "react";
import "./App.css";
import { api } from "./api";
import { useLiveSocket } from "./hooks/useLiveSocket";
import { useI18n } from "./i18n/I18nContext";
import ThemeToggle from "./components/ThemeToggle";

import Dashboard from "./components/Dashboard";
import LiveMap from "./components/LiveMap";
import RoutePlanner from "./components/RoutePlanner";
import SOSPanel from "./components/SOSPanel";
import ReportForm from "./components/ReportForm";
import AlertsFeed from "./components/AlertsFeed";
import NotifyOutbox from "./components/NotifyOutbox";

const TAB_KEYS = [
  { key: "dashboard", navKey: "dashboard", icon: "📊" },
  { key: "map", navKey: "map", icon: "🗺️" },
  { key: "routes", navKey: "routes", icon: "🧭" },
  { key: "sos", navKey: "sos", icon: "🚨" },
  { key: "report", navKey: "report", icon: "📷" },
  { key: "alerts", navKey: "alerts", icon: "🔔" },
  { key: "notify", navKey: "notify", icon: "💬" },
];

export default function App() {
  const [tab, setTab] = useState("dashboard");
  const { t, lang, setLang, languages, languageLabels } = useI18n();

  const [staticNodes, setStaticNodes] = useState([]);
  const [sensors, setSensors] = useState([]);
  const [edges, setEdges] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [reports, setReports] = useState([]);
  const [sosEvents, setSosEvents] = useState([]);
  const [summary, setSummary] = useState(null);

  const refreshAll = useCallback(() => {
    api.listNodes().then(setStaticNodes).catch(() => {});
    api.listSensors().then(setSensors).catch(() => {});
    api.graphSnapshot().then((g) => setEdges(g.edges)).catch(() => {});
    api.listVehicles().then(setVehicles).catch(() => {});
    api.listAlerts().then(setAlerts).catch(() => {});
    api.listReports().then(setReports).catch(() => {});
    api.listSOS().then(setSosEvents).catch(() => {});
    api.dashboardSummary().then(setSummary).catch(() => {});
  }, []);

  useEffect(() => {
    refreshAll();
    const t = setInterval(() => {
      api.graphSnapshot().then((g) => setEdges(g.edges)).catch(() => {});
      api.dashboardSummary().then(setSummary).catch(() => {});
    }, 5000);
    return () => clearInterval(t);
  }, [refreshAll]);

  const connected = useLiveSocket((event) => {
    if (event.kind === "sensor_update") {
      const s = event.data;
      setSensors((prev) => {
        const idx = prev.findIndex((p) => p.node_key === s.node_key);
        if (idx === -1) return [...prev, s];
        const next = [...prev];
        next[idx] = s;
        return next;
      });
    } else if (event.kind === "vehicle_update") {
      const v = event.data;
      setVehicles((prev) => {
        const idx = prev.findIndex((p) => p.id === v.id);
        if (idx === -1) return [...prev, v];
        const next = [...prev];
        next[idx] = v;
        return next;
      });
    } else if (event.kind === "alert") {
      setAlerts((prev) => [event.data, ...prev].slice(0, 300));
    } else if (event.kind === "ground_report") {
      setReports((prev) => [event.data, ...prev].slice(0, 300));
    } else if (event.kind === "sos" || event.kind === "sos_resolved") {
      const e = event.data;
      setSosEvents((prev) => {
        const idx = prev.findIndex((p) => p.id === e.id);
        if (idx === -1) return [e, ...prev];
        const next = [...prev];
        next[idx] = e;
        return next;
      });
    }
  });

  async function resolveSOS(id) {
    await api.resolveSOS(id);
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="topbar__brand">
          <div className="topbar__brand-mark">⛰️</div>
          {t("common.appName")}
        </div>
        <div className="topbar__spacer" />
        <ThemeToggle />
        <select className="lang-select" value={lang} onChange={(e) => setLang(e.target.value)}>
          {languages.map((code) => (
            <option key={code} value={code}>{languageLabels[code]}</option>
          ))}
        </select>
        <div className="conn-pill">
          <span className={`conn-dot ${connected ? "live" : ""}`} />
          {connected ? t("common.live") : t("common.reconnecting")}
        </div>
      </header>

      <nav className="sidebar">
        {TAB_KEYS.map((tb) => (
          <button
            key={tb.key}
            className={`nav-btn ${tab === tb.key ? "active" : ""}`}
            onClick={() => setTab(tb.key)}
          >
            <span className="nav-icon">{tb.icon}</span>
            {t(`nav.${tb.navKey}`)}
          </button>
        ))}
        <div className="sidebar__footer">
          {t("common.sidebarFooter1")}<br />
          {t("common.sidebarFooter2")}
        </div>
      </nav>

      <main className="main">
        {tab === "dashboard" && <Dashboard summary={summary} alerts={alerts} />}
        {tab === "map" && <LiveMap nodes={sensors} edges={edges} vehicles={vehicles} reports={reports} />}
        {tab === "routes" && <RoutePlanner nodes={staticNodes} edges={edges} vehicles={vehicles} />}
        {tab === "sos" && <SOSPanel vehicles={vehicles} sosEvents={sosEvents} onResolved={resolveSOS} />}
        {tab === "report" && <ReportForm reports={reports} onSubmitted={refreshAll} />}
        {tab === "alerts" && <AlertsFeed alerts={alerts} />}
        {tab === "notify" && <NotifyOutbox />}
      </main>
    </div>
  );
}
