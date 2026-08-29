import { createContext, useContext, useEffect, useState } from "react";
import { TRANSLATIONS, LANGUAGES, LANGUAGE_LABELS } from "./translations";

const STORAGE_KEY = "ner-logisense-lang";
const I18nContext = createContext(null);

function getByPath(obj, path) {
  return path.split(".").reduce((acc, key) => (acc && acc[key] !== undefined ? acc[key] : undefined), obj);
}

function interpolate(str, vars) {
  if (!vars) return str;
  return str.replace(/\{\{(\w+)\}\}/g, (_, key) => (vars[key] !== undefined ? vars[key] : `{{${key}}}`));
}

export function I18nProvider({ children }) {
  const [lang, setLang] = useState(() => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      return LANGUAGES.includes(stored) ? stored : "en";
    } catch {
      return "en";
    }
  });

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, lang);
    } catch {
      // storage unavailable -- language just won't persist across reloads
    }
    document.documentElement.setAttribute("lang", lang);
  }, [lang]);

  function t(path, vars) {
    const value =
      getByPath(TRANSLATIONS[lang], path) ??
      getByPath(TRANSLATIONS.en, path) ??
      path;
    return typeof value === "string" ? interpolate(value, vars) : value;
  }

  return (
    <I18nContext.Provider value={{ lang, setLang, t, languages: LANGUAGES, languageLabels: LANGUAGE_LABELS }}>
      {children}
    </I18nContext.Provider>
  );
}

export function useI18n() {
  const ctx = useContext(I18nContext);
  if (!ctx) throw new Error("useI18n must be used within an I18nProvider");
  return ctx;
}
