package com.ner.logistics.tracking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingKafkaProducer trackingKafkaProducer;
    private final RedisTrackingService redisTrackingService;
    private final VehicleLocationRepository vehicleLocationRepository;

    @PostMapping("/location")
    public ResponseEntity<GpsLocationDto> ingestLocation(@Valid @RequestBody GpsLocationDto dto) {
        if (dto.getTimestamp() == null) {
            dto.setTimestamp(LocalDateTime.now());
        }

        // Publish to Kafka pipeline
        trackingKafkaProducer.publishLocationUpdate(dto);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/latest/{vehicleCode}")
    public ResponseEntity<GpsLocationDto> getLatestLocation(@PathVariable String vehicleCode) {
        return redisTrackingService.getLatestLocation(vehicleCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history/{vehicleCode}")
    public ResponseEntity<List<VehicleLocation>> getLocationHistory(@PathVariable String vehicleCode) {
        List<VehicleLocation> history = vehicleLocationRepository.findByVehicleCodeOrderByTimestampDesc(vehicleCode);
        return ResponseEntity.ok(history);
    }
}
