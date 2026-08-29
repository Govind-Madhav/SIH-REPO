package com.ner.logistics.vehicle;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{code}")
    public ResponseEntity<Vehicle> getVehicleByCode(@PathVariable String code) {
        return ResponseEntity.ok(vehicleService.getVehicleByCode(code));
    }

    @PutMapping("/{code}/status")
    public ResponseEntity<Vehicle> updateVehicleStatus(@PathVariable String code, @RequestParam String status) {
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(code, status));
    }
}
