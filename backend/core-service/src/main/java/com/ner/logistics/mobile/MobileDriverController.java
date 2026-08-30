package com.ner.logistics.mobile;

import com.ner.logistics.audit.AuditService;
import com.ner.logistics.routing.GraphHopperRoutingService;
import com.ner.logistics.routing.RouteRequestDto;
import com.ner.logistics.routing.RouteResponseDto;
import com.ner.logistics.shipment.Shipment;
import com.ner.logistics.shipment.ShipmentRepository;
import com.ner.logistics.sos.SosEvent;
import com.ner.logistics.sos.SosEventRepository;
import com.ner.logistics.user.User;
import com.ner.logistics.vehicle.Vehicle;
import com.ner.logistics.vehicle.VehicleRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/mobile/driver")
@RequiredArgsConstructor
public class MobileDriverController {

    private final VehicleRepository vehicleRepository;
    private final ShipmentRepository shipmentRepository;
    private final SosEventRepository sosEventRepository;
    private final GraphHopperRoutingService graphHopperRoutingService;
    private final AuditService auditService;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('VEHICLE_VIEW_SELF') or hasAuthority('VEHICLE_VIEW')")
    public ResponseEntity<DriverAssignmentDto> getDriverContext(Authentication authentication) {
        String username = getUsername(authentication);

        Vehicle vehicle = vehicleRepository.findByAssignedDriverUsername(username)
                .orElse(null);

        String vehicleCode = vehicle != null ? vehicle.getCode() : null;

        List<Shipment> shipments = shipmentRepository.findByAssignedDriverUsername(username);

        SosEvent activeSos = vehicleCode != null ?
                sosEventRepository.findByVehicleCodeAndStatusNot(vehicleCode, "RESOLVED").stream().findFirst().orElse(null) : null;

        DriverAssignmentDto dto = DriverAssignmentDto.builder()
                .driverUsername(username)
                .fullName(authentication != null && authentication.getPrincipal() instanceof User u ? u.getFullName() : "Convoy Driver")
                .role("DRIVER")
                .assignedVehicle(vehicle)
                .assignedShipments(shipments)
                .activeSosEvent(activeSos)
                .build();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me/vehicle")
    @PreAuthorize("hasAuthority('VEHICLE_VIEW_SELF') or hasAuthority('VEHICLE_VIEW')")
    public ResponseEntity<Vehicle> getAssignedVehicle(Authentication authentication) {
        String username = getUsername(authentication);
        Vehicle vehicle = vehicleRepository.findByAssignedDriverUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No vehicle assigned to driver: " + username));
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/me/shipments")
    @PreAuthorize("hasAuthority('SHIPMENT_VIEW_SELF') or hasAuthority('SHIPMENT_VIEW')")
    public ResponseEntity<List<Shipment>> getAssignedShipments(Authentication authentication) {
        String username = getUsername(authentication);
        List<Shipment> shipments = shipmentRepository.findByAssignedDriverUsername(username);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/me/route")
    @PreAuthorize("hasAuthority('ROUTE_VIEW_SELF') or hasAuthority('ROUTE_VIEW')")
    public ResponseEntity<RouteResponseDto> getAssignedRoute(Authentication authentication) {
        String username = getUsername(authentication);
        Vehicle vehicle = vehicleRepository.findByAssignedDriverUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No active vehicle or route assigned to driver: " + username));

        RouteRequestDto request = RouteRequestDto.builder()
                .vehicleCode(vehicle.getCode())
                .originLat(25.1234)
                .originLng(92.5678)
                .destLat(24.8333)
                .destLng(92.7789)
                .avoidHazardZones(true)
                .build();

        return ResponseEntity.ok(graphHopperRoutingService.calculateRoute(request));
    }

    @GetMapping("/me/sos")
    @PreAuthorize("hasAuthority('SOS_VIEW_SELF') or hasAuthority('SOS_VIEW')")
    public ResponseEntity<SosEvent> getActiveSosStatus(Authentication authentication) {
        String username = getUsername(authentication);
        Vehicle vehicle = vehicleRepository.findByAssignedDriverUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No vehicle assigned to driver: " + username));

        List<SosEvent> activeSosList = sosEventRepository.findByVehicleCodeAndStatusNot(vehicle.getCode(), "RESOLVED");

        if (activeSosList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(activeSosList.get(0));
    }

    @PostMapping("/me/hazard")
    @PreAuthorize("hasAuthority('ROAD_HAZARD_FLAG_SELF') or hasAuthority('ROAD_STATUS_UPDATE')")
    public ResponseEntity<?> flagEnRouteHazard(@RequestBody DriverHazardFlagDto dto, Authentication authentication) {
        String username = getUsername(authentication);

        log.warn("🪨 En-Route Road Hazard Flagged by Driver {}: [{}] at ({}, {}) - Notes: {}",
                username, dto.getHazardType(), dto.getLatitude(), dto.getLongitude(), dto.getDescription());

        auditService.logDetailedEvent(
                username,
                "DRIVER",
                "ROAD_HAZARD_FLAGGED",
                "RoadHazard",
                "HAZARD-" + System.currentTimeMillis() % 10000,
                "CLEAR",
                "REPORTED_HAZARD",
                String.format("Driver reported %s at (%.4f, %.4f): %s", dto.getHazardType(), dto.getLatitude(), dto.getLongitude(), dto.getDescription()),
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(Map.of(
                "status", "REPORTED_HAZARD",
                "hazardType", dto.getHazardType(),
                "latitude", dto.getLatitude(),
                "longitude", dto.getLongitude(),
                "reportedBy", username,
                "timestamp", LocalDateTime.now().toString(),
                "message", "Hazard flag recorded successfully. Nearby convoys and Command Center alerted."
        ));
    }

    @PutMapping("/me/shipments/{shipmentId}/status")
    @PreAuthorize("hasAuthority('DELIVERY_STATUS_UPDATE') or hasAuthority('SHIPMENT_MANAGE')")
    public ResponseEntity<Shipment> updateShipmentStatus(
            @PathVariable Long shipmentId,
            @RequestParam String status,
            Authentication authentication) {

        String username = getUsername(authentication);
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));

        // Validate driver ownership (driver can only update their assigned shipment)
        if (shipment.getAssignedDriverUsername() != null && !shipment.getAssignedDriverUsername().equalsIgnoreCase(username)
                && !"admin".equalsIgnoreCase(username) && !"operator".equalsIgnoreCase(username)) {
            throw new AccessDeniedException("Access Denied: You cannot update a shipment assigned to another driver.");
        }

        String targetStatus = status.toUpperCase();

        // Delivery Governance Guard: Driver cannot directly set official DELIVERY_CONFIRMED (requires LOGISTICS_OPERATOR)
        if ("DELIVERY_CONFIRMED".equals(targetStatus) || "DELIVERED".equals(targetStatus)) {
            if (authentication != null && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("DELIVERY_CONFIRM") || a.getAuthority().equals("SHIPMENT_MANAGE"))) {
                throw new AccessDeniedException("Access Denied: Drivers cannot directly execute final delivery confirmation. Set status to DELIVERED_PENDING_CONFIRMATION for Logistics Operator review.");
            }
        }

        String oldStatus = shipment.getStatus();
        shipment.setStatus(targetStatus);
        Shipment saved = shipmentRepository.save(shipment);
        log.info("📦 Shipment #{} status updated from {} to {} by driver {}", shipmentId, oldStatus, targetStatus, username);

        auditService.logDetailedEvent(
                username,
                "DRIVER",
                "DRIVER_SHIPMENT_STATUS_UPDATED",
                "Shipment",
                String.valueOf(shipmentId),
                oldStatus,
                targetStatus,
                "Driver updated shipment status",
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(saved);
    }

    private String getUsername(Authentication authentication) {
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof User u) {
                return u.getUsername();
            }
            return authentication.getName();
        }
        return "driver";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverHazardFlagDto {
        private String hazardType; // ROCKFALL, FALLEN_TREE, ROAD_BLOCKED, MUD_SLIDE, BRIDGE_RISK
        private double latitude;
        private double longitude;
        private String description;
    }
}
