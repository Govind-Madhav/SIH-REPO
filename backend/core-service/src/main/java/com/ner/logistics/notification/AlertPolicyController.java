package com.ner.logistics.notification;

import com.ner.logistics.audit.AuditService;
import com.ner.logistics.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications/policies")
@RequiredArgsConstructor
public class AlertPolicyController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('ALERT_POLICY_MANAGE') or hasAuthority('SYSTEM_CONFIG_MANAGE')")
    public ResponseEntity<List<AlertPolicyDto>> getAlertPolicies() {
        List<AlertPolicyDto> policies = List.of(
                AlertPolicyDto.builder()
                        .policyId("POL-CRITICAL-DISRUPTION")
                        .severityLevel("CRITICAL")
                        .recipientRoles(List.of("EMERGENCY_OPERATOR", "LOGISTICS_OPERATOR", "FIELD_OFFICER", "DRIVER"))
                        .escalationDelayMinutes(0)
                        .supportedLanguages(List.of("en", "hi", "as", "bn", "khasi"))
                        .templateFormat("CRITICAL LANDSLIDE: Corridor {corridorCode} BLOCKED. Rerouting active.")
                        .build(),
                AlertPolicyDto.builder()
                        .policyId("POL-WEATHER-WARNING")
                        .severityLevel("HIGH")
                        .recipientRoles(List.of("LOGISTICS_OPERATOR", "DRIVER"))
                        .escalationDelayMinutes(15)
                        .supportedLanguages(List.of("en", "hi", "as", "bn"))
                        .templateFormat("WEATHER WARNING: Heavy Rainfall (>{rainfallMm}mm) reported in {district}.")
                        .build()
        );
        return ResponseEntity.ok(policies);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ALERT_POLICY_MANAGE') or hasAuthority('SYSTEM_CONFIG_MANAGE')")
    public ResponseEntity<?> updateAlertPolicy(@RequestBody AlertPolicyDto dto, @AuthenticationPrincipal User actor) {
        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "ALERT_POLICY_UPDATED",
                "AlertPolicy",
                dto.getPolicyId(),
                "Previous Policy Configuration",
                dto.getTemplateFormat(),
                "Operational escalation policy update",
                null,
                "SUCCESS"
        );
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Alert policy " + dto.getPolicyId() + " updated successfully."));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertPolicyDto {
        private String policyId;
        private String severityLevel;
        private List<String> recipientRoles;
        private int escalationDelayMinutes;
        private List<String> supportedLanguages;
        private String templateFormat;
    }
}
