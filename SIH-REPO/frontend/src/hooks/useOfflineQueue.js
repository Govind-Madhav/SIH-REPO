import { useCallback, useEffect, useState } from "react";

const STORAGE_KEY = "ner-logisense-offline-report-queue";

function loadQueue() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");
  } catch {
    return [];
  }
}

function saveQueue(queue) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(queue));
  } catch {
    // storage unavailable (private mode etc) -- degrade to in-memory only
  }
}

/**
 * Field officials in low/no-network zones still need to file a report.
 * This queues the (JSON-serialisable) report payload in localStorage and
 * flushes it through `submitFn` the moment the browser regains connectivity
 * or the app is reopened -- the "offline sync" requirement from the brief,
 * without needing a full service-worker/PWA build for this prototype.
 */
export function useOfflineQueue(submitFn) {
  const [queue, setQueue] = useState(loadQueue);
  const [online, setOnline] = useState(navigator.onLine);

  useEffect(() => saveQueue(queue), [queue]);

  const flush = useCallback(async () => {
    if (!navigator.onLine) return;
    const current = loadQueue();
    if (current.length === 0) return;
    const remaining = [];
    for (const item of current) {
      try {
        await submitFn(item);
      } catch {
        remaining.push(item);
      }
    }
    setQueue(remaining);
  }, [submitFn]);

  useEffect(() => {
    const goOnline = () => {
      setOnline(true);
      flush();
    };
    const goOffline = () => setOnline(false);
    window.addEventListener("online", goOnline);
    window.addEventListener("offline", goOffline);
    flush();
    return () => {
      window.removeEventListener("online", goOnline);
      window.removeEventListener("offline", goOffline);
    };
  }, [flush]);

  const enqueue = useCallback((item) => {
    setQueue((q) => [...q, { ...item, _queuedAt: new Date().toISOString() }]);
  }, []);

  return { queue, online, enqueue, flush };
}
