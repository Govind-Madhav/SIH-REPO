package com.ner.logistics.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttSensorIngestionAdapter {

    private final SimpMessagingTemplate messagingTemplate;

    public SensorReadingDto processSensorReading(SensorReadingDto dto) {
        if (dto.getTimestamp() == null) {
            dto.setTimestamp(LocalDateTime.now());
        }

        log.info("📡 MQTT Sensor Pipeline: Processed sensor reading type={} id={} value={} {}",
                dto.getSensorType(), dto.getSensorId(), dto.getReadingValue(), dto.getUnit());

        // Broadcast high-priority sensor alert via WebSocket if warning or critical
        if ("WARNING".equalsIgnoreCase(dto.getStatus()) || "CRITICAL".equalsIgnoreCase(dto.getStatus())) {
            messagingTemplate.convertAndSend("/topic/sensor-alerts", dto);
        }

        return dto;
    }

    public List<SensorReadingDto> getActiveSensorReadings() {
        List<SensorReadingDto> list = new ArrayList<>();

        list.add(SensorReadingDto.builder()
                .sensorId("SENS-HAFLONG-RAIN-01")
                .sensorType("RAINFALL")
                .locationName("Haflong Pass Sector 4")
                .latitude(25.1234)
                .longitude(92.5678)
                .readingValue(138.5)
                .unit("mm/24h")
                .status("CRITICAL")
                .timestamp(LocalDateTime.now())
                .build());

        list.add(SensorReadingDto.builder()
                .sensorId("SENS-HAFLONG-SOIL-02")
                .sensorType("SOIL_MOISTURE")
                .locationName("Haflong West Slope")
                .latitude(25.1300)
                .longitude(92.5700)
                .readingValue(91.2)
                .unit("% Saturation")
                .status("CRITICAL")
                .timestamp(LocalDateTime.now())
                .build());

        list.add(SensorReadingDto.builder()
                .sensorId("SENS-UMRANGSO-VIB-03")
                .sensorType("GROUND_VIBRATION")
                .locationName("Umrangso Valley Bridge")
                .latitude(25.5000)
                .longitude(92.6000)
                .readingValue(0.12)
                .unit("mm/s")
                .status("NORMAL")
                .timestamp(LocalDateTime.now())
                .build());

        return list;
    }
}
