package com.ner.logistics.accessibility.geofence;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emergency/corridors")
@RequiredArgsConstructor
public class EmergencyCorridorController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('EMERGENCY_CORRIDOR_MANAGE') or hasAuthority('ROAD_STATUS_VIEW')")
    public ResponseEntity<List<EmergencyCorridorRestrictionDto>> getEmergencyCorridors() {
        List<EmergencyCorridorRestrictionDto> restrictions = List.of(
                EmergencyCorridorRestrictionDto.builder()
                        .restrictionId("RSTR-NH27-HAFLONG")
                        .corridorCode("COR-NH27")
                        .corridorName("NH-27 Haflong Mountain Pass")
                        .restrictionType("EMERGENCY_ONLY")
                        .status("ACTIVE")
                        .declaredBy("EMERGENCY_OPERATOR_01")
                        .declaredAt(LocalDateTime.now().minusHours(1).toString())
                        .expiresAt(LocalDateTime.now().plusHours(5).toString()) // 6-hour restriction
                        .justificationReason("Priority clearance for NDRF rescue team and ambulances")
                        .build()
        );
        return ResponseEntity.ok(restrictions);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMERGENCY_CORRIDOR_MANAGE')")
    public ResponseEntity<?> declareEmergencyCorridor(@RequestBody EmergencyCorridorRestrictionDto dto,
                                                       @AuthenticationPrincipal User actor) {
        if (dto.getJustificationReason() == null || dto.getJustificationReason().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mandatory justification reason is required for declaring emergency corridor restrictions."));
        }

        if (dto.getExpiresAt() == null) {
            dto.setExpiresAt(LocalDateTime.now().plusHours(6).toString()); // Default 6 hour expiry
        }
        dto.setStatus("ACTIVE");
        dto.setDeclaredBy(actor != null ? actor.getUsername() : "EMERGENCY_OPERATOR");
        dto.setDeclaredAt(LocalDateTime.now().toString());

        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "EMERGENCY_OPERATOR",
                actor != null ? actor.getRole().name() : "EMERGENCY_OPERATOR",
                "EMERGENCY_CORRIDOR_RESTRICTED",
                "EmergencyCorridor",
                dto.getCorridorCode(),
                "ACCESSIBLE",
                dto.getRestrictionType(),
                dto.getJustificationReason(),
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(dto);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyCorridorRestrictionDto {
        private String restrictionId;
        private String corridorCode;
        private String corridorName;
        private String restrictionType; // EMERGENCY_ONLY, BLOCKED, PRIORITY_CORRIDOR, ACCESSIBLE
        private String status;          // ACTIVE, EXPIRED, REMOVED
        private String declaredBy;
        private String declaredAt;
        private String expiresAt;
        private String justificationReason;
    }
}
