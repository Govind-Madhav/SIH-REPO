package com.ner.logistics.tracking;

import com.ner.logistics.incident.Incident;
import com.ner.logistics.incident.IncidentRepository;
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
    private final IncidentRepository incidentRepository;

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

    @GetMapping("/safety-bubble/{vehicleCode}")
    public ResponseEntity<VehicleSafetyBubbleDto> getVehicleSafetyBubble(@PathVariable String vehicleCode) {
        List<Incident> activeIncidents = incidentRepository.findByStatus("ACTIVE");

        String zone = "SAFE_ZONE";
        double distance = 18.5;
        String hazard = "NONE";
        String action = "Proceed along assigned route at standard speed";

        if (!activeIncidents.isEmpty()) {
            zone = "DANGER_ZONE";
            distance = 3.2;
            hazard = activeIncidents.get(0).getType();
            action = "⚠️ APPROACHING HAZARD: Prepare to reduce speed and switch to Haflong Bypass Corridor";
        }

        VehicleSafetyBubbleDto bubble = VehicleSafetyBubbleDto.builder()
                .vehicleCode(vehicleCode)
                .safetyZone(zone)
                .distanceToHazardKm(distance)
                .hazardType(hazard)
                .recommendedDriverAction(action)
                .build();

        return ResponseEntity.ok(bubble);
    }
}
