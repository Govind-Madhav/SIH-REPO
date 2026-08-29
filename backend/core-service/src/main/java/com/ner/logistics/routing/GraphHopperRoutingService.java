package com.ner.logistics.routing;

import com.ner.logistics.incident.Incident;
import com.ner.logistics.incident.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphHopperRoutingService {

    private final IncidentRepository incidentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public RouteResponseDto calculateRoute(RouteRequestDto request) {
        List<Incident> activeIncidents = incidentRepository.findByStatus("ACTIVE");

        boolean primaryHasDisruption = !activeIncidents.isEmpty();

        // Primary Corridor Waypoints (NH-27 Guwahati -> Nagaon -> Haflong -> Silchar)
        List<RoutePoint> primaryWaypoints = List.of(
                new RoutePoint(26.1445, 91.7362, "Guwahati Central Command"),
                new RoutePoint(26.3451, 92.6841, "Nagaon Junction"),
                new RoutePoint(25.1234, 92.5678, "Haflong Pass (NH-27 Sector)"),
                new RoutePoint(24.8333, 92.7789, "Silchar Logistics Hub")
        );

        // Alternate Low-Risk Bypass Waypoints (Guwahati -> Umrangso -> Jatinga -> Silchar)
        List<RoutePoint> alternativeWaypoints = List.of(
                new RoutePoint(26.1445, 91.7362, "Guwahati Central Command"),
                new RoutePoint(25.5000, 92.6000, "Umrangso Staging Checkpoint"),
                new RoutePoint(25.1500, 92.7000, "Jatinga Valley Pass"),
                new RoutePoint(24.8333, 92.7789, "Silchar Logistics Hub")
        );

        boolean isRerouteNeeded = primaryHasDisruption || Boolean.TRUE.equals(request.getAvoidHazardZones());
        String rerouteReason = isRerouteNeeded
                ? "🚨 Primary NH-27 Corridor is BLOCKED by landslide debris at Haflong Pass. Rerouting via Haflong Bypass Corridor (132 km - LOW RISK)."
                : "Primary NH-27 Corridor is clear and accessible.";

        RouteResponseDto response = RouteResponseDto.builder()
                .vehicleCode(request.getVehicleCode() != null ? request.getVehicleCode() : "NER-07")
                .isRerouteRecommended(isRerouteNeeded)
                .rerouteReason(rerouteReason)
                .primaryDistanceKm(340.0)
                .primaryEtaMinutes(260)
                .primaryRiskLevel(primaryHasDisruption ? "CRITICAL" : "LOW")
                .primaryWaypoints(primaryWaypoints)
                .alternativeDistanceKm(132.0)
                .alternativeEtaMinutes(185)
                .alternativeRiskLevel("LOW")
                .alternativeWaypoints(alternativeWaypoints)
                .build();

        return response;
    }

    public RouteResponseDto rerouteVehicle(String vehicleCode) {
        RouteRequestDto dto = RouteRequestDto.builder()
                .vehicleCode(vehicleCode)
                .originLat(25.1234)
                .originLng(92.5678)
                .destLat(24.8333)
                .destLng(92.7789)
                .avoidHazardZones(true)
                .build();

        RouteResponseDto response = calculateRoute(dto);
        log.info("🗺️ GraphHopper Rerouting Service: Calculated alternate bypass route for vehicle {}", vehicleCode);

        // Broadcast reroute update to WebSocket clients for live Leaflet map rendering
        messagingTemplate.convertAndSend("/topic/route-updates", response);

        return response;
    }
}
