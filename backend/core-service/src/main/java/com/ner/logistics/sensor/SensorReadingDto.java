package com.ner.logistics.sensor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorReadingDto {

    private String sensorId; // e.g. SENS-HAFLONG-01

    private String sensorType; // RAINFALL, SOIL_MOISTURE, WATER_LEVEL, GROUND_VIBRATION, ROAD_CONDITION

    private String locationName; // Haflong Pass Sector 4

    private Double latitude;

    private Double longitude;

    private Double readingValue;

    private String unit; // mm/h, %, meters, mm/s^2, status_score

    private String status; // NORMAL, WARNING, CRITICAL

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
