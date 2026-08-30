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
        return logDetailedEvent(actor, actorRole, action, resourceType, resourceId, null, null, null, null, result);
    }

    @Transactional
    public AuditEvent logDetailedEvent(String actor, String actorRole, String action, String resourceType,
                                        String resourceId, String oldValue, String newValue,
                                        String justificationReason, String clientIp, String result) {
        AuditEvent event = AuditEvent.builder()
                .actor(actor != null ? actor : "SYSTEM")
                .actorRole(actorRole)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .oldValue(oldValue)
                .newValue(newValue)
                .justificationReason(justificationReason)
                .clientIp(clientIp)
                .result(result != null ? result : "SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();

        AuditEvent saved = auditEventRepository.save(event);
        log.info("📝 Audit Logger: Logged action={} actor={} resource={}:{} reason={}", action, actor, resourceType, resourceId, justificationReason);
        return saved;
    }

    public List<AuditEvent> getAuditEvents() {
        return auditEventRepository.findAll();
    }
}
