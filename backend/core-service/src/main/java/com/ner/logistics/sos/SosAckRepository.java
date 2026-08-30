package com.ner.logistics.sos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SosAckRepository extends JpaRepository<SosAck, Long> {
    Optional<SosAck> findByMeshPacketId(String meshPacketId);
}
