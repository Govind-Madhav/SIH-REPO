package com.ner.logistics.decision;

import com.ner.logistics.audit.AuditService;
import com.ner.logistics.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
public class LogisticsDecisionController {

    private final LogisticsDecisionService logisticsDecisionService;
    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('DECISION_VIEW') or hasAuthority('ROUTE_VIEW') or hasAuthority('SHIPMENT_VIEW')")
    public ResponseEntity<List<DecisionRecommendationDto>> getRecommendations() {
        return ResponseEntity.ok(logisticsDecisionService.getRecommendations());
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('DECISION_APPROVE') or hasAuthority('ROUTE_APPROVE')")
    public ResponseEntity<?> approveDecision(@RequestBody DecisionApprovalDto dto,
                                              @AuthenticationPrincipal User actor) {
        // Security Boundary Enforcement: Logistics Operator cannot approve emergency rescue escalation
        if ("ESCALATE_EMERGENCY".equalsIgnoreCase(dto.getDecisionType())) {
            return ResponseEntity.status(403).body(Map.of("error", "Access Denied: ESCALATE_EMERGENCY decisions belong exclusively to EMERGENCY_OPERATOR role."));
        }

        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "LOGISTICS_OPERATOR",
                actor != null ? actor.getRole().name() : "LOGISTICS_OPERATOR",
                "DECISION_APPROVED",
                "DecisionRecommendation",
                dto.getDecisionType() + ":" + dto.getTargetEntity(),
                "RECOMMENDED",
                "APPROVED",
                dto.getJustificationReason() != null ? dto.getJustificationReason() : "Operator approved AI logistics recommendation",
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "decisionType", dto.getDecisionType(),
                "targetEntity", dto.getTargetEntity(),
                "approvedBy", actor != null ? actor.getUsername() : "LOGISTICS_OPERATOR",
                "approvedAt", LocalDateTime.now().toString(),
                "message", "Logistics operational decision approved and dispatched to transport fleet."
        ));
    }

    @Data
    public static class DecisionApprovalDto {
        private String decisionType; // REROUTE_VEHICLE, HOLD_SHIPMENT, PRIORITIZE_SHIPMENT, PROCEED_WITH_CAUTION
        private String targetEntity;
        private String justificationReason;
    }
}
