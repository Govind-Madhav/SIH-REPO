import { createContext, useContext, useEffect, useState } from "react";

const STORAGE_KEY = "ner-logisense-theme"; // stored value: 'light' | 'dark' | 'system'
const ThemeContext = createContext(null);

function applyDomTheme(mode) {
  const root = document.documentElement;
  if (mode === "system") root.removeAttribute("data-theme");
  else root.setAttribute("data-theme", mode);
}

export function ThemeProvider({ children }) {
  const [mode, setMode] = useState(() => {
    try {
      return localStorage.getItem(STORAGE_KEY) || "system";
    } catch {
      return "system";
    }
  });
  const [systemDark, setSystemDark] = useState(
    () => window.matchMedia?.("(prefers-color-scheme: dark)").matches ?? true
  );

  useEffect(() => {
    applyDomTheme(mode);
    try {
      localStorage.setItem(STORAGE_KEY, mode);
    } catch {
      // storage unavailable -- theme just won't persist across reloads
    }
  }, [mode]);

  useEffect(() => {
    const mq = window.matchMedia?.("(prefers-color-scheme: dark)");
    if (!mq) return;
    const handler = (e) => setSystemDark(e.matches);
    mq.addEventListener("change", handler);
    return () => mq.removeEventListener("change", handler);
  }, []);

  const effective = mode === "system" ? (systemDark ? "dark" : "light") : mode;

  return (
    <ThemeContext.Provider value={{ mode, setMode, effective }}>
      {children}
    </ThemeContext.Provider>
  );
}

export function useTheme() {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error("useTheme must be used within a ThemeProvider");
  return ctx;
}
