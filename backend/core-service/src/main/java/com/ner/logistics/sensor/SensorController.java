package com.ner.logistics.sensor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final MqttSensorIngestionAdapter sensorAdapter;

    @PostMapping("/ingest")
    public ResponseEntity<SensorReadingDto> ingestSensorReading(@RequestBody SensorReadingDto dto) {
        return ResponseEntity.ok(sensorAdapter.processSensorReading(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SENSOR_ALERT_VIEW') or hasAuthority('ALERT_VIEW') or hasAuthority('INTEGRATION_HEALTH_VIEW')")
    public ResponseEntity<List<SensorReadingDto>> getSensorReadings() {
        return ResponseEntity.ok(sensorAdapter.getActiveSensorReadings());
    }

    @GetMapping("/alerts/nearby")
    @PreAuthorize("hasAuthority('SENSOR_ALERT_VIEW') or hasAuthority('ALERT_VIEW')")
    public ResponseEntity<List<SensorAlertSummaryDto>> getNearbySensorAlerts(@RequestParam double lat,
                                                                              @RequestParam double lng,
                                                                              @RequestParam(defaultValue = "15000") double distanceMeters) {
        List<SensorAlertSummaryDto> alerts = List.of(
                SensorAlertSummaryDto.builder()
                        .sensorId("SENS-SOIL-DIMA-88")
                        .sensorType("SOIL_MOISTURE_TILT")
                        .locationName("NH-27 Haflong Mountain Slope KM-42")
                        .districtName("Dima Hasao")
                        .latitude(lat)
                        .longitude(lng)
                        .moisturePercentage(94.2)
                        .tiltDegrees(14.8)
                        .alertLevel("CRITICAL_LANDSLIDE_RISK")
                        .recommendedAction("Dispatch Field Officer immediately for physical verification")
                        .triggeredAt(LocalDateTime.now().minusMinutes(12).toString())
                        .build()
        );
        return ResponseEntity.ok(alerts);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SensorAlertSummaryDto {
        private String sensorId;
        private String sensorType;
        private String locationName;
        private String districtName;
        private double latitude;
        private double longitude;
        private double moisturePercentage;
        private double tiltDegrees;
        private String alertLevel;
        private String recommendedAction;
        private String triggeredAt;
    }
}
