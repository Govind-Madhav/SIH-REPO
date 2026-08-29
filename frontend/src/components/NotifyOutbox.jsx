import { useEffect, useState } from "react";
import { api } from "../api";
import { Badge, useTimeAgo } from "./common";
import { useI18n } from "../i18n/I18nContext";

export default function NotifyOutbox() {
  const { t } = useI18n();
  const timeAgo = useTimeAgo();
  const [outbox, setOutbox] = useState([]);
  const [subscribers, setSubscribers] = useState([]);

  useEffect(() => {
    const load = () => {
      api.outbox().then(setOutbox).catch(() => {});
      api.subscribers().then(setSubscribers).catch(() => {});
    };
    load();
    const t = setInterval(load, 5000);
    return () => clearInterval(t);
  }, []);

  const simulated = outbox.some((m) => m.status === "SIMULATED");

  return (
    <div>
      <h1 className="page-title">{t("notify.title")}</h1>
      <p className="page-subtitle">{t("notify.subtitle")}</p>

      {simulated && (
        <div className="banner warn">{t("notify.simulatedBanner")}</div>
      )}

      <div className="two-col">
        <div className="panel">
          <div className="panel__title">{t("notify.outboxTitle", { count: outbox.length })}</div>
          <div style={{ maxHeight: 480, overflowY: "auto" }}>
            <table className="table">
              <thead>
                <tr>
                  <th>{t("notify.colChannel")}</th>
                  <th>{t("notify.colTo")}</th>
                  <th>{t("notify.colMessage")}</th>
                  <th>{t("notify.colStatus")}</th>
                  <th>{t("notify.colWhen")}</th>
                </tr>
              </thead>
              <tbody>
                {outbox.map((m) => (
                  <tr key={m.id}>
                    <td>{m.channel === "whatsapp" ? "🟢 WhatsApp" : "✉️ SMS"}</td>
                    <td>{m.to}</td>
                    <td style={{ maxWidth: 340 }}>{m.body}</td>
                    <td><Badge tone={m.status}>{m.status}</Badge></td>
                    <td>{timeAgo(m.created_at)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="panel">
          <div className="panel__title">{t("notify.subscribersTitle", { count: subscribers.length })}</div>
          <table className="table">
            <thead>
              <tr>
                <th>{t("notify.colName")}</th>
                <th>{t("notify.colDistrict")}</th>
                <th>{t("notify.colRole")}</th>
                <th>{t("notify.colChannels")}</th>
              </tr>
            </thead>
            <tbody>
              {subscribers.map((s, i) => (
                <tr key={i}>
                  <td>{s.name}</td>
                  <td>{s.district}</td>
                  <td>{s.role}</td>
                  <td>{s.channels.join(", ")}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
