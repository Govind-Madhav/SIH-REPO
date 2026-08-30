package com.ner.logistics.mobile;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/mobile/driver")
@RequiredArgsConstructor
public class MobileDriverController {

    private final VehicleRepository vehicleRepository;
    private final ShipmentRepository shipmentRepository;
    private final SosEventRepository sosEventRepository;
    private final GraphHopperRoutingService graphHopperRoutingService;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('VEHICLE_VIEW_SELF') or hasAuthority('VEHICLE_VIEW')")
    public ResponseEntity<DriverAssignmentDto> getDriverContext(Authentication authentication) {
        String username = getUsername(authentication);

        Vehicle vehicle = vehicleRepository.findByAssignedDriverUsername(username)
                .or(() -> vehicleRepository.findByCode("NER-07"))
                .orElse(null);

        String vehicleCode = vehicle != null ? vehicle.getCode() : "NER-07";

        List<Shipment> shipments = shipmentRepository.findByAssignedDriverUsername(username);
        if (shipments.isEmpty()) {
            shipments = shipmentRepository.findByVehicleCode(vehicleCode);
        }

        List<SosEvent> activeSosList = sosEventRepository.findByVehicleCodeAndStatusNot(vehicleCode, "RESOLVED");
        SosEvent activeSos = activeSosList.isEmpty() ? null : activeSosList.get(0);

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
                .or(() -> vehicleRepository.findByCode("NER-07"))
                .orElseThrow(() -> new IllegalArgumentException("No vehicle assigned to driver: " + username));
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/me/shipments")
    @PreAuthorize("hasAuthority('SHIPMENT_VIEW_SELF') or hasAuthority('SHIPMENT_VIEW')")
    public ResponseEntity<List<Shipment>> getAssignedShipments(Authentication authentication) {
        String username = getUsername(authentication);
        List<Shipment> shipments = shipmentRepository.findByAssignedDriverUsername(username);
        if (shipments.isEmpty()) {
            shipments = shipmentRepository.findByVehicleCode("NER-07");
        }
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/me/route")
    @PreAuthorize("hasAuthority('ROUTE_VIEW')")
    public ResponseEntity<RouteResponseDto> getAssignedRoute(Authentication authentication) {
        String username = getUsername(authentication);
        Vehicle vehicle = vehicleRepository.findByAssignedDriverUsername(username)
                .or(() -> vehicleRepository.findByCode("NER-07"))
                .orElse(null);

        String code = vehicle != null ? vehicle.getCode() : "NER-07";

        RouteRequestDto request = RouteRequestDto.builder()
                .vehicleCode(code)
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
                .or(() -> vehicleRepository.findByCode("NER-07"))
                .orElse(null);

        String code = vehicle != null ? vehicle.getCode() : "NER-07";
        List<SosEvent> activeSosList = sosEventRepository.findByVehicleCodeAndStatusNot(code, "RESOLVED");

        if (activeSosList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(activeSosList.get(0));
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

        shipment.setStatus(status.toUpperCase());
        Shipment saved = shipmentRepository.save(shipment);
        log.info("📦 Shipment #{} status updated to {} by driver {}", shipmentId, status, username);

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
}
