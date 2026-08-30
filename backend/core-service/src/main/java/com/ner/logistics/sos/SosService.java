package com.ner.logistics.sos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SosService {

    private final SosEventRepository sosEventRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional
    public SosEvent triggerSos(SosRequestDto dto, String username) {
        Point spatialPoint = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        spatialPoint.setSRID(4326);

        SosEvent event = SosEvent.builder()
                .triggeredBy(username != null ? username : "DRIVER_CONVOY")
                .vehicleCode(dto.getVehicleCode() != null ? dto.getVehicleCode() : "NER-07")
                .location(spatialPoint)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .emergencyType(dto.getEmergencyType() != null ? dto.getEmergencyType() : "LANDSLIDE_TRAPPED")
                .message(dto.getMessage())
                .status("ACTIVE")
                .deliveryType("DIRECT_CELLULAR")
                .originTimestamp(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        SosEvent savedEvent = sosEventRepository.save(event);
        log.warn("🚨 DIRECT SOS TRIGGERED by user={}, vehicle={}, location=({}, {})", username, dto.getVehicleCode(), dto.getLatitude(), dto.getLongitude());

        // Instant Emergency Broadcast via WebSockets
        messagingTemplate.convertAndSend("/topic/sos-alerts", savedEvent);

        return savedEvent;
    }

    @Transactional
    public SosEvent processRelayedSos(SosRelayRequestDto dto) {
        Point spatialPoint = geometryFactory.createPoint(new Coordinate(dto.getOriginLongitude(), dto.getOriginLatitude()));
        spatialPoint.setSRID(4326);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime originTime = dto.getOriginTimestamp() != null ? dto.getOriginTimestamp() : now.minusMinutes(15);
        long latency = Math.max(0, Duration.between(originTime, now).toMinutes());

        SosEvent event = SosEvent.builder()
                .meshPacketId(dto.getMeshPacketId())
                .triggeredBy("OFFLINE_SHADOW_ZONE_DRIVER")
                .vehicleCode(dto.getOriginVehicleCode())
                .location(spatialPoint)
                .latitude(dto.getOriginLatitude())
                .longitude(dto.getOriginLongitude())
                .emergencyType(dto.getEmergencyType() != null ? dto.getEmergencyType() : "LANDSLIDE_TRAPPED")
                .message(dto.getMessage() != null ? dto.getMessage() : "Offline P2P Mesh SOS: Vehicle trapped in zero connectivity shadow zone")
                .status("ACTIVE")
                .deliveryType("MESH_RELAY_STORE_FORWARD")
                .relayedByVehicle(dto.getRelayedByVehicleCode())
                .relayHopCount(dto.getHopCount() != null ? dto.getHopCount() : 1)
                .relayLatencyMinutes(latency)
                .originTimestamp(originTime)
                .createdAt(now)
                .build();

        SosEvent savedEvent = sosEventRepository.save(event);
        log.warn("⚡ MESH RELAYED SOS RECEIVED: Origin Vehicle={}, Trapped Location=({}, {}), Carried & Flushed by Vehicle={}, Latency={} mins",
                dto.getOriginVehicleCode(), dto.getOriginLatitude(), dto.getOriginLongitude(), dto.getRelayedByVehicleCode(), latency);

        // Broadcast High-Priority Mesh SOS Alert over WebSockets
        messagingTemplate.convertAndSend("/topic/sos-alerts", savedEvent);

        return savedEvent;
    }

    public List<SosEvent> getActiveSosEvents() {
        return sosEventRepository.findByStatus("ACTIVE");
    }
}
