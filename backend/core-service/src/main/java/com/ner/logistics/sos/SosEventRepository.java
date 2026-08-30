package com.ner.logistics.sos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SosEventRepository extends JpaRepository<SosEvent, Long> {
    List<SosEvent> findByStatus(String status);
    List<SosEvent> findByStatusNot(String status);
    List<SosEvent> findByVehicleCodeAndStatusNot(String vehicleCode, String status);
    Optional<SosEvent> findByMeshPacketId(String meshPacketId);
}

