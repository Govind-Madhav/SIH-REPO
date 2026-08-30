package com.ner.logistics.vehicle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleByCode(String code) {
        return vehicleRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with code: " + code));
    }

    @Transactional
    public Vehicle updateVehicleStatus(String code, String status) {
        Vehicle vehicle = getVehicleByCode(code);
        vehicle.setStatus(status.toUpperCase());
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle assignDriverToVehicle(String vehicleCode, String driverUsername) {
        // Step 2 Validation: Check if driver is already assigned to another active vehicle
        Optional<Vehicle> existingAssignment = vehicleRepository.findByAssignedDriverUsername(driverUsername);
        if (existingAssignment.isPresent() && !existingAssignment.get().getCode().equals(vehicleCode)) {
            throw new IllegalArgumentException("Driver " + driverUsername + " is already assigned to active vehicle " + existingAssignment.get().getCode());
        }

        Vehicle vehicle = getVehicleByCode(vehicleCode);
        vehicle.setAssignedDriverUsername(driverUsername);
        vehicle.setStatus("ASSIGNED");
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle unassignDriver(String vehicleCode) {
        Vehicle vehicle = getVehicleByCode(vehicleCode);
        vehicle.setAssignedDriverUsername(null);
        vehicle.setStatus("AVAILABLE");
        return vehicleRepository.save(vehicle);
    }
}
