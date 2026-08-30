package com.ner.logistics.weather;

public interface WeatherAdapter {
    WeatherDataDto getWeatherForDistrict(String district);
    boolean isAvailable();
}
