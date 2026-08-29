package com.ner.logistics.incident;

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
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional
    public Incident createIncident(CreateIncidentDto dto, String username) {
        Point spatialPoint = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        spatialPoint.setSRID(4326);

        Incident incident = Incident.builder()
                .type(dto.getType().toUpperCase())
                .severity(dto.getSeverity().toUpperCase())
                .description(dto.getDescription())
                .location(spatialPoint)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .reportedBy(username != null ? username : "ANONYMOUS_FIELD_OFFICER")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        Incident savedIncident = incidentRepository.save(incident);
        log.info("Created incident id={}, type={}, severity={}", savedIncident.getId(), savedIncident.getType(), savedIncident.getSeverity());

        // Broadcast to WebSocket clients
        messagingTemplate.convertAndSend("/topic/incident-events", savedIncident);

        return savedIncident;
    }

    public List<Incident> getActiveIncidents() {
        return incidentRepository.findByStatus("ACTIVE");
    }

    public List<Incident> getNearbyIncidents(double lat, double lng, double distanceMeters) {
        return incidentRepository.findIncidentsNearLocation(lat, lng, distanceMeters);
    }

    @Transactional
    public Incident updateIncidentStatus(Long id, String status) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));

        incident.setStatus(status.toUpperCase());
        Incident updatedIncident = incidentRepository.save(incident);

        messagingTemplate.convertAndSend("/topic/incident-events", updatedIncident);
        return updatedIncident;
    }
}
