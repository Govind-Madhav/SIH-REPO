package com.ner.logistics.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    @Transactional
    public AuditEvent logEvent(String actor, String actorRole, String action, String resourceType, String resourceId, String result) {
        AuditEvent event = AuditEvent.builder()
                .actor(actor != null ? actor : "SYSTEM")
                .actorRole(actorRole)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .result(result != null ? result : "SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();

        AuditEvent saved = auditEventRepository.save(event);
        log.info("📝 Audit Logger: Logged event action={} actor={} resource={}:{}", action, actor, resourceType, resourceId);
        return saved;
    }

    public List<AuditEvent> getAuditEvents() {
        return auditEventRepository.findAll();
    }
}
