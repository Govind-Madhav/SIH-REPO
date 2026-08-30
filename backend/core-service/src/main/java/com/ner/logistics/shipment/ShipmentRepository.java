package com.ner.logistics.shipment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByVehicleCodeIn(List<String> vehicleCodes);
    List<Shipment> findByVehicleCode(String vehicleCode);
    List<Shipment> findByAssignedDriverUsername(String username);
}
