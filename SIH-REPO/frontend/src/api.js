import axios from "axios";

export const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8000";

const client = axios.create({ baseURL: API_BASE });

export const api = {
  health: () => client.get("/api/health").then((r) => r.data),

  listSensors: () => client.get("/api/sensors").then((r) => r.data),

  predictRisk: (payload) => client.post("/api/risk/predict", payload).then((r) => r.data),
  featureImportance: () => client.get("/api/risk/feature-importance").then((r) => r.data),

  listNodes: () => client.get("/api/routes/nodes").then((r) => r.data),
  graphSnapshot: () => client.get("/api/routes/graph").then((r) => r.data),
  planRoute: (origin, destination, k = 3) =>
    client.get("/api/routes/plan", { params: { origin, destination, k } }).then((r) => r.data),

  listVehicles: () => client.get("/api/vehicles").then((r) => r.data),

  listAlerts: (activeOnly = false) =>
    client.get("/api/alerts", { params: { active_only: activeOnly } }).then((r) => r.data),

  listReports: () => client.get("/api/reports").then((r) => r.data),
  submitReport: (formData) =>
    client.post("/api/reports", formData, { headers: { "Content-Type": "multipart/form-data" } }).then((r) => r.data),

  sendSOS: (payload) => client.post("/api/sos", payload).then((r) => r.data),
  listSOS: () => client.get("/api/sos").then((r) => r.data),
  resolveSOS: (id) => client.post(`/api/sos/${id}/resolve`).then((r) => r.data),

  dashboardSummary: () => client.get("/api/dashboard/summary").then((r) => r.data),

  outbox: () => client.get("/api/notify/outbox").then((r) => r.data),
  subscribers: () => client.get("/api/notify/subscribers").then((r) => r.data),
  subscribe: (payload) => client.post("/api/notify/subscribe", payload).then((r) => r.data),

  i18n: (lang) => client.get(`/api/i18n/${lang}`).then((r) => r.data),
};

export function wsUrl() {
  return API_BASE.replace(/^http/, "ws") + "/ws";
}
