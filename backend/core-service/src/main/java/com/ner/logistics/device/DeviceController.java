package com.ner.logistics.device;

import com.ner.logistics.audit.AuditService;
import com.ner.logistics.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('DEVICE_STATUS_VIEW') or hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        // Security Rule: Strip hashed API key from GET responses
        devices.forEach(d -> d.setApiKeyHash(null));
        return ResponseEntity.ok(devices);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<Device> registerDevice(
            @RequestParam String imei,
            @RequestParam(required = false) String vehicleCode,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String deviceType,
            @RequestParam String apiKey) {
        Device device = deviceService.registerDevice(imei, vehicleCode, deviceName, deviceType, apiKey);
        device.setApiKeyHash(null);
        return ResponseEntity.ok(device);
    }

    @PostMapping("/{imei}/assign/{vehicleCode}")
    @PreAuthorize("hasAuthority('DEVICE_ASSIGN') or hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<Device> assignDeviceToVehicle(@PathVariable String imei,
                                                        @PathVariable String vehicleCode,
                                                        @AuthenticationPrincipal User actor) {
        Device device = deviceService.assignDeviceToVehicle(imei, vehicleCode);
        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "DEVICE_ASSIGNED",
                "Device",
                imei,
                "UNASSIGNED",
                vehicleCode,
                "Assigned device " + imei + " to vehicle " + vehicleCode,
                null,
                "SUCCESS"
        );
        device.setApiKeyHash(null);
        return ResponseEntity.ok(device);
    }

    @PostMapping("/{imei}/unassign")
    @PreAuthorize("hasAuthority('DEVICE_UNASSIGN') or hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<Device> unassignDevice(@PathVariable String imei,
                                                 @AuthenticationPrincipal User actor) {
        Device device = deviceService.unassignDevice(imei);
        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "DEVICE_UNASSIGNED",
                "Device",
                imei,
                device.getVehicleCode(),
                "UNASSIGNED",
                "Unassigned device " + imei,
                null,
                "SUCCESS"
        );
        device.setApiKeyHash(null);
        return ResponseEntity.ok(device);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('DEVICE_MANAGE')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody DeviceStatusDto dto,
                                          @AuthenticationPrincipal User actor) {
        if ("REVOKED".equalsIgnoreCase(dto.getStatus()) && (dto.getJustificationReason() == null || dto.getJustificationReason().trim().isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mandatory justification reason is required for device revocation."));
        }

        Device device = deviceService.updateDeviceStatus(id, dto.getStatus());
        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "DEVICE_STATUS_CHANGED",
                "Device",
                id.toString(),
                "ACTIVE",
                dto.getStatus(),
                dto.getJustificationReason() != null ? dto.getJustificationReason() : "Status updated to " + dto.getStatus(),
                null,
                "SUCCESS"
        );
        device.setApiKeyHash(null);
        return ResponseEntity.ok(device);
    }

    @Data
    public static class DeviceStatusDto {
        private String status;
        private String justificationReason;
    }
}
