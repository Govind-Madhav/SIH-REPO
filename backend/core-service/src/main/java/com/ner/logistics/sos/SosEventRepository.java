package com.ner.logistics.sos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SosEventRepository extends JpaRepository<SosEvent, Long> {
    List<SosEvent> findByStatus(String status);
}
