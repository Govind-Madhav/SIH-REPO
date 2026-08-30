package com.ner.logistics.weather;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherIntegrationService {

    public WeatherDataDto getDistrictWeather(String district) {
        String targetDistrict = district != null ? district : "Dima Hasao";

        // Simulated Weather Data Provider with explicit SIMULATED_FALLBACK tag
        WeatherDataDto weather = WeatherDataDto.builder()
                .district(targetDistrict)
                .rainfallMm24h(142.5)
                .rainfallTrend("INCREASING")
                .temperatureCelsius(22.4)
                .weatherCondition("TORRENTIAL_DOWNPOUR")
                .dataSource("SIMULATED_FALLBACK")
                .timestamp(LocalDateTime.now())
                .build();

        log.info("🌦️ Weather Service: Fetched weather observation for district {} [Source: {}]", targetDistrict, weather.getDataSource());
        return weather;
    }
}
