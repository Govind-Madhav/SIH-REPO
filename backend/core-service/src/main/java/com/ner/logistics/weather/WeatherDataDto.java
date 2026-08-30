package com.ner.logistics.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDataDto {

    private String district;

    private Double rainfallMm24h;

    private String rainfallTrend; // INCREASING, STABLE, DECREASING

    private Double temperatureCelsius;

    private String weatherCondition; // HEAVY_RAINFALL, TORRENTIAL_DOWNPOUR, CLEAR, FOG

    private String dataSource; // REAL_TIME_API, SIMULATED_FALLBACK

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
