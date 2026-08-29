package com.ner.logistics.vehicle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleByCode(String code) {
        return vehicleRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with code: " + code));
    }

    @Transactional
    public Vehicle updateVehicleStatus(String code, String status) {
        Vehicle vehicle = getVehicleByCode(code);
        vehicle.setStatus(status.toUpperCase());
        return vehicleRepository.save(vehicle);
    }
}
