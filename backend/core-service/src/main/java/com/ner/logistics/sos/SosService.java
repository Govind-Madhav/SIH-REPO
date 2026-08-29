package com.ner.logistics.sos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .createdAt(LocalDateTime.now())
                .build();

        SosEvent savedEvent = sosEventRepository.save(event);
        log.warn("🚨 EMERGENCY SOS TRIGGERED by user={}, vehicle={}, location=({}, {})", username, dto.getVehicleCode(), dto.getLatitude(), dto.getLongitude());

        // Instant Emergency Broadcast via WebSockets
        messagingTemplate.convertAndSend("/topic/sos-alerts", savedEvent);

        return savedEvent;
    }

    public List<SosEvent> getActiveSosEvents() {
        return sosEventRepository.findByStatus("ACTIVE");
    }
}
