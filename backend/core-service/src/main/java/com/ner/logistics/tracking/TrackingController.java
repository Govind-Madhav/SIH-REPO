package com.ner.logistics.tracking;

import com.ner.logistics.device.DeviceService;
import com.ner.logistics.incident.Incident;
import com.ner.logistics.incident.IncidentRepository;
import com.ner.logistics.sos.SosService;
import com.ner.logistics.user.User;
import com.ner.logistics.user.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingKafkaProducer trackingKafkaProducer;
    private final RedisTrackingService redisTrackingService;
    private final VehicleLocationRepository vehicleLocationRepository;
    private final IncidentRepository incidentRepository;
    private final DeviceService deviceService;
    private final SosService sosService;

    // Cache for clientEventId deduplication
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    @PostMapping("/location")
    public ResponseEntity<GpsLocationDto> ingestLocation(@Valid @RequestBody GpsLocationDto dto) {
        if (dto.getTimestamp() == null) {
            dto.setTimestamp(LocalDateTime.now());
        }

        // Publish to Kafka pipeline
        trackingKafkaProducer.publishLocationUpdate(dto);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/location/batch")
    public ResponseEntity<List<GpsLocationDto>> ingestBatchLocations(@RequestBody BatchGpsLocationDto batchDto) {
        List<GpsLocationDto> processed = new ArrayList<>();

        if (batchDto.getEvents() != null) {
            for (GpsLocationDto dto : batchDto.getEvents()) {
                if (dto.getVehicleCode() == null && batchDto.getVehicleCode() != null) {
                    dto.setVehicleCode(batchDto.getVehicleCode());
                }

                // Idempotency check using clientEventId or timestamp hash
                String eventKey = dto.getVehicleCode() + "_" + (dto.getTimestamp() != null ? dto.getTimestamp().toString() : System.currentTimeMillis());
                if (processedEventIds.contains(eventKey)) {
                    log.info("ℹ️ Skipped duplicate offline telemetry fix for vehicle {}", dto.getVehicleCode());
                    continue;
                }
                processedEventIds.add(eventKey);

                if (dto.getTimestamp() == null) {
                    dto.setTimestamp(LocalDateTime.now());
                }

                trackingKafkaProducer.publishLocationUpdate(dto);
                processed.add(dto);
            }
        }

        log.info("🔄 Processed offline batch telemetry: {} fix(s) ingested", processed.size());
        return ResponseEntity.ok(processed);
    }

    @PostMapping("/telematics/ais140")
    public ResponseEntity<?> ingestHardwareTelematics(
            @Valid @RequestBody HardwareTelematicsIngestDto dto,
            @RequestHeader(value = "X-DEVICE-KEY", required = false) String deviceKey) {

        // Validate Device Security & Status
        boolean isValid = deviceService.validateAndTouchDevice(dto.getImei(), deviceKey);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Device authentication failed or device status is REVOKED/INACTIVE");
        }

        String code = dto.getVehicleCode() != null ? dto.getVehicleCode() : "NER-" + dto.getImei().substring(Math.max(0, dto.getImei().length() - 2));

        GpsLocationDto gpsDto = GpsLocationDto.builder()
                .vehicleCode(code)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .speedKmh(dto.getSpeedKmh() != null ? dto.getSpeedKmh() : 0.0)
                .headingDegrees(dto.getHeadingDegrees() != null ? dto.getHeadingDegrees() : 0.0)
                .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                .build();

        // Publish directly into Kafka high-frequency streaming pipeline
        trackingKafkaProducer.publishLocationUpdate(gpsDto);

        // Hardware Panic Wire Check -> Trigger SOS if pressed
        if (Boolean.TRUE.equals(dto.getSosButtonPressed())) {
            log.warn("🚨 HARDWARE SOS PANIC BUTTON DETECTED on Telematics Unit IMEI={} Vehicle={}", dto.getImei(), code);
            sosService.processHardwareSosTrigger(code, dto.getLatitude(), dto.getLongitude());
        }

        return ResponseEntity.ok(gpsDto);
    }

    @GetMapping("/latest/{vehicleCode}")
    public ResponseEntity<GpsLocationDto> getLatestLocation(@PathVariable String vehicleCode, Authentication auth) {
        validateDriverSelfAccess(vehicleCode, auth);
        return redisTrackingService.getLatestLocation(vehicleCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history/{vehicleCode}")
    public ResponseEntity<List<VehicleLocation>> getLocationHistory(@PathVariable String vehicleCode, Authentication auth) {
        validateDriverSelfAccess(vehicleCode, auth);
        List<VehicleLocation> history = vehicleLocationRepository.findByVehicleCodeOrderByTimestampDesc(vehicleCode);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/safety-bubble/{vehicleCode}")
    public ResponseEntity<VehicleSafetyBubbleDto> getVehicleSafetyBubble(@PathVariable String vehicleCode, Authentication auth) {
        validateDriverSelfAccess(vehicleCode, auth);
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

    private void validateDriverSelfAccess(String requestedVehicleCode, Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof User user) {
            if (user.getRole() == UserRole.DRIVER) {
                // Driver NER-07 can only view vehicle NER-07
                String assignedVehicle = "NER-07"; // Primary assigned vehicle for demo driver account
                if (!assignedVehicle.equalsIgnoreCase(requestedVehicleCode)) {
                    log.warn("⛔ ACCESS DENIED: Driver {} attempted to view unauthorized vehicle {}", user.getUsername(), requestedVehicleCode);
                    throw new AccessDeniedException("Access Denied: Drivers are restricted to viewing their assigned vehicle (" + assignedVehicle + ")");
                }
            }
        }
    }
}
