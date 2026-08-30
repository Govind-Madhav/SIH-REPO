package com.ner.logistics.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<SensorReadingDto>> getSensorReadings() {
        return ResponseEntity.ok(sensorAdapter.getActiveSensorReadings());
    }
}
