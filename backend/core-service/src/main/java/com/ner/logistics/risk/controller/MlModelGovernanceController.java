package com.ner.logistics.risk.controller;

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
@RequestMapping("/api/ml")
@RequiredArgsConstructor
public class MlModelGovernanceController {

    private final AuditService auditService;

    @GetMapping("/models")
    @PreAuthorize("hasAuthority('MODEL_VERSION_VIEW') or hasAuthority('MODEL_PERFORMANCE_VIEW') or hasAuthority('SYSTEM_CONFIG_MANAGE')")
    public ResponseEntity<List<MlModelMetadataDto>> getModelMetadata() {
        List<MlModelMetadataDto> models = List.of(
                MlModelMetadataDto.builder()
                        .modelId("xgb-landslide-ner-v2.1")
                        .modelName("XGBoost Landslide Hazard Predictor")
                        .activeVersion("2.1.0")
                        .status("DEPLOYED")
                        .f1Score(0.942)
                        .rocAuc(0.965)
                        .recall(0.928)
                        .lastTrainedAt(LocalDateTime.now().minusDays(5).toString())
                        .deployedAt(LocalDateTime.now().minusDays(2).toString())
                        .build(),
                MlModelMetadataDto.builder()
                        .modelId("lgb-flood-impact-v1.8")
                        .modelName("LightGBM Flood Impact Evaluator")
                        .activeVersion("1.8.4")
                        .status("DEPLOYED")
                        .f1Score(0.918)
                        .rocAuc(0.941)
                        .recall(0.905)
                        .lastTrainedAt(LocalDateTime.now().minusDays(10).toString())
                        .deployedAt(LocalDateTime.now().minusDays(4).toString())
                        .build()
        );
        return ResponseEntity.ok(models);
    }

    @PostMapping("/models/deploy")
    @PreAuthorize("hasAuthority('MODEL_DEPLOY') or hasAuthority('SYSTEM_CONFIG_MANAGE')")
    public ResponseEntity<?> deployModel(@RequestBody ModelActionDto dto, @AuthenticationPrincipal User actor) {
        if (dto.getJustificationReason() == null || dto.getJustificationReason().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mandatory justification reason is required for ML model deployment."));
        }

        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "ML_MODEL_DEPLOYED",
                "MLModel",
                dto.getModelId(),
                "v2.0.0",
                dto.getVersionToDeploy(),
                dto.getJustificationReason(),
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "ML Model " + dto.getModelId() + " version " + dto.getVersionToDeploy() + " deployed successfully.",
                "deployedAt", LocalDateTime.now().toString()
        ));
    }

    @PostMapping("/models/rollback")
    @PreAuthorize("hasAuthority('MODEL_ROLLBACK') or hasAuthority('SYSTEM_CONFIG_MANAGE')")
    public ResponseEntity<?> rollbackModel(@RequestBody ModelActionDto dto, @AuthenticationPrincipal User actor) {
        if (dto.getJustificationReason() == null || dto.getJustificationReason().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mandatory justification reason is required for ML model rollback."));
        }

        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "ML_MODEL_ROLLED_BACK",
                "MLModel",
                dto.getModelId(),
                "v2.1.0",
                dto.getVersionToDeploy(),
                dto.getJustificationReason(),
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "ML Model " + dto.getModelId() + " rolled back to version " + dto.getVersionToDeploy(),
                "rolledBackAt", LocalDateTime.now().toString()
        ));
    }

    @PutMapping("/thresholds")
    @PreAuthorize("hasAuthority('RISK_THRESHOLD_MANAGE') or hasAuthority('SYSTEM_CONFIG_MANAGE')")
    public ResponseEntity<?> updateRiskThresholds(@RequestBody RiskThresholdConfigDto dto, @AuthenticationPrincipal User actor) {
        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "RISK_THRESHOLDS_UPDATED",
                "RiskThreshold",
                "GLOBAL_CONFIG",
                "Default Thresholds",
                "RainfallThreshold=" + dto.getRainfallMm24hThreshold() + "mm",
                dto.getJustificationReason() != null ? dto.getJustificationReason() : "Operational adjustment",
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Operational risk thresholds updated successfully."));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MlModelMetadataDto {
        private String modelId;
        private String modelName;
        private String activeVersion;
        private String status;
        private double f1Score;
        private double rocAuc;
        private double recall;
        private String lastTrainedAt;
        private String deployedAt;
    }

    @Data
    public static class ModelActionDto {
        private String modelId;
        private String versionToDeploy;
        private String justificationReason;
    }

    @Data
    public static class RiskThresholdConfigDto {
        private double rainfallMm24hThreshold;
        private double landslideProbabilityThreshold;
        private String justificationReason;
    }
}
