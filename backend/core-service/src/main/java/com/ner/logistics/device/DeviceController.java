package com.ner.logistics.device;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LOGISTICS_OPERATOR')")
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> registerDevice(
            @RequestParam String imei,
            @RequestParam String vehicleCode,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String deviceType,
            @RequestParam String apiKey) {
        Device device = deviceService.registerDevice(imei, vehicleCode, deviceName, deviceType, apiKey);
        return ResponseEntity.ok(device);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(deviceService.updateDeviceStatus(id, status));
    }
}

