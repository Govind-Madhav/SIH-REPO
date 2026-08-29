import { useEffect, useRef, useState } from "react";
import { wsUrl } from "../api";

/**
 * Connects to the backend WebSocket and fans out events by `kind`
 * (sensor_update, alert, vehicle_update, sos, sos_resolved, ground_report).
 * Auto-reconnects with backoff so a restarted backend or a dropped Wi-Fi
 * link recovers without a page reload.
 */
export function useLiveSocket(onEvent) {
  const [connected, setConnected] = useState(false);
  const handlerRef = useRef(onEvent);
  handlerRef.current = onEvent;

  useEffect(() => {
    let ws;
    let retryDelay = 1000;
    let cancelled = false;
    let retryTimer;

    function connect() {
      ws = new WebSocket(wsUrl());

      ws.onopen = () => {
        setConnected(true);
        retryDelay = 1000;
      };
      ws.onmessage = (evt) => {
        try {
          const parsed = JSON.parse(evt.data);
          handlerRef.current?.(parsed);
        } catch {
          // ignore malformed frame
        }
      };
      ws.onclose = () => {
        setConnected(false);
        if (!cancelled) {
          retryTimer = setTimeout(connect, retryDelay);
          retryDelay = Math.min(retryDelay * 1.6, 15000);
        }
      };
      ws.onerror = () => ws.close();
    }

    connect();
    return () => {
      cancelled = true;
      clearTimeout(retryTimer);
      ws?.close();
    };
  }, []);

  return connected;
}
