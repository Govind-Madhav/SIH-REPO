package com.ner.logistics.governance;

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
@RequestMapping("/api/governance")
@RequiredArgsConstructor
public class DataGovernanceController {

    private final AuditService auditService;

    @GetMapping("/data-retention")
    @PreAuthorize("hasAuthority('DATA_RETENTION_MANAGE') or hasAuthority('ARCHIVE_STATUS_VIEW') or hasAuthority('SYSTEM_CONFIG_MANAGE')")
    public ResponseEntity<List<DataRetentionPolicyDto>> getRetentionPolicies() {
        List<DataRetentionPolicyDto> policies = List.of(
                DataRetentionPolicyDto.builder()
                        .datasetName("GPS_TELEMETRY_RAW")
                        .hotStorageDays(90)
                        .archiveStorageDays(365)
                        .deletionPolicy("POLICY_CONTROLLED_ARCHIVE_CLEANUP")
                        .build(),
                DataRetentionPolicyDto.builder()
                        .datasetName("INCIDENT_REPORTS_AND_EVIDENCE")
                        .hotStorageDays(365)
                        .archiveStorageDays(1825) // 5 years
                        .deletionPolicy("PERMANENT_GOVERNMENT_RECORD_KEEPING")
                        .build(),
                DataRetentionPolicyDto.builder()
                        .datasetName("AUDIT_SECURITY_LOGS")
                        .hotStorageDays(180)
                        .archiveStorageDays(2555) // 7 years CERT-In compliance
                        .deletionPolicy("IMMUTABLE_AUDIT_ARCHIVE")
                        .build()
        );
        return ResponseEntity.ok(policies);
    }

    @PutMapping("/data-retention")
    @PreAuthorize("hasAuthority('DATA_RETENTION_MANAGE') or hasAuthority('SYSTEM_CONFIG_MANAGE')")
    public ResponseEntity<?> updateRetentionPolicy(@RequestBody DataRetentionPolicyDto dto,
                                                    @AuthenticationPrincipal User actor) {
        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "DATA_RETENTION_POLICY_UPDATED",
                "DataRetentionPolicy",
                dto.getDatasetName(),
                "Previous Policy",
                "HotStorage=" + dto.getHotStorageDays() + "d, Archive=" + dto.getArchiveStorageDays() + "d",
                "Compliance policy update",
                null,
                "SUCCESS"
        );
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Data retention policy for " + dto.getDatasetName() + " updated."));
    }

    @PostMapping("/export-sensitive")
    @PreAuthorize("hasAuthority('SENSITIVE_DATA_EXPORT') or hasAuthority('REPORT_EXPORT')")
    public ResponseEntity<?> exportSensitiveData(@RequestBody SensitiveExportRequestDto dto,
                                                 @AuthenticationPrincipal User actor) {
        if (dto.getJustificationReason() == null || dto.getJustificationReason().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mandatory justification reason is required for sensitive data export."));
        }

        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "SENSITIVE_DATA_EXPORTED",
                "Dataset",
                dto.getDatasetType(),
                null,
                "Exported Records Count: " + dto.getRecordCount(),
                dto.getJustificationReason(),
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "exportId", "EXP-" + System.currentTimeMillis(),
                "datasetType", dto.getDatasetType(),
                "exportedAt", LocalDateTime.now().toString(),
                "message", "Export request authorized and logged for audit compliance."
        ));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataRetentionPolicyDto {
        private String datasetName;
        private int hotStorageDays;
        private int archiveStorageDays;
        private String deletionPolicy;
    }

    @Data
    public static class SensitiveExportRequestDto {
        private String datasetType; // DRIVER_GPS_HISTORY, DEVICE_IMEI_LIST, USER_AUDIT_LOGS
        private int recordCount;
        private String justificationReason;
    }
}
