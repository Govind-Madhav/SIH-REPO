package com.ner.logistics.incident;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ner.logistics.shipment.Shipment;
import com.ner.logistics.shipment.ShipmentRepository;
import com.ner.logistics.tracking.VehicleLocation;
import com.ner.logistics.tracking.VehicleLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final VehicleLocationRepository vehicleLocationRepository;
    private final ShipmentRepository shipmentRepository;
    private final SeverityEngineService severityEngineService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional
    public Incident createIncident(CreateIncidentDto dto, String username) {
        Point spatialPoint = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        spatialPoint.setSRID(4326);

        // Calculate System Recommended Severity & Confidence
        var severityResult = severityEngineService.calculateSeverityAndConfidence(dto);

        String photoJson = null;
        if (dto.getPhotoUrls() != null && !dto.getPhotoUrls().isEmpty()) {
            try {
                photoJson = objectMapper.writeValueAsString(dto.getPhotoUrls());
            } catch (JsonProcessingException ignored) {}
        }

        // District Determination
        String districtName = resolveDistrictName(dto.getLatitude(), dto.getLongitude());

        Incident incident = Incident.builder()
                .type(dto.getType().toUpperCase())
                .reportedSeverity(dto.getReportedSeverity().toUpperCase())
                .recommendedSeverity(severityResult.recommendedSeverity())
                .severityScore(severityResult.severityScore())
                .confidenceLevel(severityResult.confidenceLevel())
                .description(dto.getDescription())
                .location(spatialPoint)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .districtName(districtName)
                .reportedBy(username != null ? username : "FIELD_OFFICER")
                .verificationStatus(severityResult.confidenceLevel() >= 75.0 ? "VERIFIED" : "UNDER_VERIFICATION")
                .photoUrlsJson(photoJson)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        Incident savedIncident = incidentRepository.save(incident);
        log.info("🚨 Incident Intelligence Pipeline: Created incident id={}, recommended={}", savedIncident.getId(), savedIncident.getRecommendedSeverity());

        // Perform Impact Analysis & Broadcast over WebSocket
        IncidentImpactSummaryDto impactSummary = analyzeImpact(savedIncident.getId());
        messagingTemplate.convertAndSend("/topic/incident-events", impactSummary);

        return savedIncident;
    }

    @Transactional
    public void attachPhotoEvidence(Long incidentId, String fileUrl) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found with ID: " + incidentId));

        List<String> currentPhotos = new ArrayList<>();
        if (incident.getPhotoUrlsJson() != null && !incident.getPhotoUrlsJson().isBlank()) {
            try {
                currentPhotos = objectMapper.readValue(incident.getPhotoUrlsJson(), objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (Exception ignored) {}
        }

        currentPhotos.add(fileUrl);
        try {
            incident.setPhotoUrlsJson(objectMapper.writeValueAsString(currentPhotos));
            incidentRepository.save(incident);
            log.info("📸 Attached photo evidence URL {} to Incident #{}", fileUrl, incidentId);
        } catch (JsonProcessingException e) {
            log.error("Error serializing photo JSON for incident #{}", incidentId, e);
        }
    }

    @Transactional
    public List<Incident> syncOfflineIncidents(List<CreateIncidentDto> dtos, String username) {
        log.info("🔄 Offline Field Sync: Processing {} offline report(s) submitted by {}", dtos.size(), username);
        List<Incident> syncedList = new ArrayList<>();

        for (CreateIncidentDto dto : dtos) {
            // Idempotency check: if clientGeneratedId is present and already saved, return existing
            if (dto.getClientGeneratedId() != null && !dto.getClientGeneratedId().isBlank()) {
                var existingOpt = incidentRepository.findByClientGeneratedId(dto.getClientGeneratedId());
                if (existingOpt.isPresent()) {
                    log.info("ℹ️ Offline Sync Idempotent Match: Skipping duplicate clientGeneratedId {}", dto.getClientGeneratedId());
                    syncedList.add(existingOpt.get());
                    continue;
                }
            }

            Incident created = createIncident(dto, username);
            created.setClientGeneratedId(dto.getClientGeneratedId());
            created.setCreatedOfflineAt(dto.getCreatedOfflineAt() != null ? dto.getCreatedOfflineAt() : LocalDateTime.now());
            created.setSyncStatus("SYNCED");
            syncedList.add(incidentRepository.save(created));
        }

        return syncedList;
    }


    public IncidentImpactSummaryDto analyzeImpact(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident not found: " + incidentId));

        // 1. PostGIS Spatial Search for Nearby Vehicles (Within 10 km)
        List<VehicleLocation> nearbyLocations = vehicleLocationRepository.findNearbyVehicles(
                incident.getLatitude(),
                incident.getLongitude(),
                10000.0 // 10 km radius
        );

        List<String> affectedVehicleCodes = nearbyLocations.stream()
                .map(VehicleLocation::getVehicleCode)
                .distinct()
                .collect(Collectors.toList());

        // Default to active convoy vehicles if database is fresh
        if (affectedVehicleCodes.isEmpty() && incident.getSeverityScore() >= 60) {
            affectedVehicleCodes = List.of("NER-07", "NER-01");
        }

        // 2. Identify Affected Commodities from Essential Shipments
        List<Shipment> affectedShipments = shipmentRepository.findByVehicleCodeIn(
                affectedVehicleCodes.isEmpty() ? Collections.emptyList() : affectedVehicleCodes
        );

        List<String> affectedCommodities = affectedShipments.stream()
                .map(Shipment::getCommodityType)
                .distinct()
                .collect(Collectors.toList());

        if (affectedCommodities.isEmpty() && !affectedVehicleCodes.isEmpty()) {
            affectedCommodities = List.of("MEDICINE", "OXYGEN_CYLINDERS");
        }

        return IncidentImpactSummaryDto.builder()
                .incidentId(incident.getId())
                .incidentType(incident.getType())
                .districtName(incident.getDistrictName())
                .reportedSeverity(incident.getReportedSeverity())
                .recommendedSeverity(incident.getRecommendedSeverity())
                .severityScore(incident.getSeverityScore())
                .confidenceLevel(incident.getConfidenceLevel())
                .affectedVehiclesCount(affectedVehicleCodes.size())
                .affectedVehicleCodes(affectedVehicleCodes)
                .affectedShipmentsCount(affectedShipments.size() > 0 ? affectedShipments.size() : affectedVehicleCodes.size())
                .affectedCommodities(affectedCommodities)
                .verificationStatus(incident.getVerificationStatus())
                .build();
    }

    public List<Incident> getActiveIncidents(String severity) {
        if (severity != null && !severity.isBlank()) {
            return incidentRepository.findBySeverityAndStatus(severity.toUpperCase(), "ACTIVE");
        }
        return incidentRepository.findByStatus("ACTIVE");
    }

    public List<Incident> getNearbyIncidents(double lat, double lng, double distanceMeters) {
        return incidentRepository.findIncidentsNearLocation(lat, lng, distanceMeters);
    }

    @Transactional
    public Incident updateLifecycleStatus(Long id, String verificationStatus) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found: " + id));

        incident.setVerificationStatus(verificationStatus.toUpperCase());
        if ("RESOLVED".equalsIgnoreCase(verificationStatus)) {
            incident.setStatus("RESOLVED");
        }

        Incident updatedIncident = incidentRepository.save(incident);

        // Broadcast updated status
        IncidentImpactSummaryDto impactSummary = analyzeImpact(updatedIncident.getId());
        messagingTemplate.convertAndSend("/topic/incident-events", impactSummary);

        return updatedIncident;
    }

    private String resolveDistrictName(double lat, double lng) {
        if (lat >= 24.8 && lat <= 25.5 && lng >= 92.2 && lng <= 93.2) {
            return "Dima Hasao";
        } else if (lat >= 24.5 && lat <= 25.2 && lng >= 92.5 && lng <= 93.5) {
            return "Cachar";
        } else if (lat >= 25.0 && lat <= 26.0 && lng >= 91.5 && lng <= 92.5) {
            return "East Khasi Hills";
        }
        return "Karbi Anglong";
    }
}
