"""
Thin wrapper around the OpenWeatherMap "Current Weather" API.

If OPENWEATHERMAP_API_KEY is not set, returns None and the simulator keeps
using its own internal rainfall model -- the app runs fine with zero keys.
"""

import os
import time
import requests

API_KEY = os.getenv("OPENWEATHERMAP_API_KEY", "").strip()
BASE_URL = "https://api.openweathermap.org/data/2.5/weather"

_cache: dict[tuple, tuple[float, dict]] = {}
_CACHE_TTL_SECONDS = 300  # avoid hammering the free-tier rate limit


async def get_current_weather(lat: float, lon: float) -> dict | None:
    if not API_KEY:
        return None

    key = (round(lat, 2), round(lon, 2))
    now = time.time()
    if key in _cache and now - _cache[key][0] < _CACHE_TTL_SECONDS:
        return _cache[key][1]

    try:
        resp = requests.get(
            BASE_URL,
            params={"lat": lat, "lon": lon, "appid": API_KEY, "units": "metric"},
            timeout=5,
        )
        resp.raise_for_status()
        data = resp.json()
        result = {
            "temperature_c": data.get("main", {}).get("temp"),
            "humidity_pct": data.get("main", {}).get("humidity"),
            "rain_1h_mm": data.get("rain", {}).get("1h"),
            "weather_main": (data.get("weather") or [{}])[0].get("main"),
            "weather_description": (data.get("weather") or [{}])[0].get("description"),
        }
        _cache[key] = (now, result)
        return result
    except requests.RequestException:
        return None
