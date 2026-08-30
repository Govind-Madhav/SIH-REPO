package com.ner.logistics.fieldtask;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/field-tasks")
@RequiredArgsConstructor
public class FieldTaskController {

    private final AuditService auditService;

    // In-memory registry tracking field inspection tasks
    private static final Map<String, FieldInspectionTaskDto> taskRegistry = new ConcurrentHashMap<>();

    static {
        taskRegistry.put("TASK-HAFLONG-104", FieldInspectionTaskDto.builder()
                .taskId("TASK-HAFLONG-104")
                .title("Inspect NH-27 Landslide & Soil Saturation Anomaly")
                .districtName("Dima Hasao")
                .assignedOfficer("FIELD_OFFICER_01")
                .targetLatitude(25.1833)
                .targetLongitude(92.8333)
                .status("RECEIVED") // CREATED, RECEIVED, ACKNOWLEDGED, EN_ROUTE, ON_SITE, COMPLETED, UNABLE_TO_REACH, REASSIGNED, CANCELLED
                .priority("HIGH")
                .instructions("IoT Sensor #SENS-SOIL-88 triggered 94.2% soil saturation alert. Confirm physical rockfall risk.")
                .assignedAt(LocalDateTime.now().minusMinutes(30).toString())
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FIELD_TASK_VIEW') or hasAuthority('INCIDENT_VIEW')")
    public ResponseEntity<List<FieldInspectionTaskDto>> getAssignedTasks(@AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(new ArrayList<>(taskRegistry.values()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FIELD_TASK_VIEW') or hasAuthority('INCIDENT_VERIFY')")
    public ResponseEntity<FieldInspectionTaskDto> createFieldTask(@RequestBody FieldInspectionTaskDto dto,
                                                                   @AuthenticationPrincipal User actor) {
        // 1️⃣ Field Task Deduplication Logic (Same district & target location within active registry)
        Optional<FieldInspectionTaskDto> duplicateOpt = taskRegistry.values().stream()
                .filter(t -> t.getDistrictName().equalsIgnoreCase(dto.getDistrictName())
                        && Math.abs(t.getTargetLatitude() - dto.getTargetLatitude()) < 0.05
                        && Math.abs(t.getTargetLongitude() - dto.getTargetLongitude()) < 0.05
                        && !"COMPLETED".equals(t.getStatus())
                        && !"CANCELLED".equals(t.getStatus()))
                .findFirst();

        if (duplicateOpt.isPresent()) {
            FieldInspectionTaskDto existing = duplicateOpt.get();
            existing.setInstructions(existing.getInstructions() + " | UPDATED ALERT: " + dto.getInstructions());
            auditService.logDetailedEvent(
                    actor != null ? actor.getUsername() : "COMMAND_CENTER",
                    actor != null ? actor.getRole().name() : "EMERGENCY_OPERATOR",
                    "FIELD_TASK_DEDUPLICATED",
                    "FieldTask",
                    existing.getTaskId(),
                    existing.getStatus(),
                    existing.getStatus(),
                    "Deduplicated repeated hazard task alert for district " + dto.getDistrictName(),
                    null,
                    "SUCCESS"
            );
            return ResponseEntity.ok(existing);
        }

        dto.setTaskId("TASK-" + (System.currentTimeMillis() % 100000));
        dto.setStatus("CREATED");
        dto.setAssignedAt(LocalDateTime.now().toString());
        taskRegistry.put(dto.getTaskId(), dto);

        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "COMMAND_CENTER",
                actor != null ? actor.getRole().name() : "EMERGENCY_OPERATOR",
                "FIELD_TASK_ASSIGNED",
                "FieldTask",
                dto.getTaskId(),
                null,
                "CREATED",
                "Assigned field inspection task to officer " + dto.getAssignedOfficer(),
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasAuthority('FIELD_TASK_ACKNOWLEDGE') or hasAuthority('FIELD_TASK_UPDATE')")
    public ResponseEntity<?> updateTaskStatus(@PathVariable String taskId,
                                              @RequestBody TaskStatusUpdateDto body,
                                              @AuthenticationPrincipal User actor) {
        FieldInspectionTaskDto task = taskRegistry.get(taskId);
        if (task == null) {
            // Create fallback task for testing
            task = FieldInspectionTaskDto.builder().taskId(taskId).status("RECEIVED").assignedOfficer("FIELD_OFFICER_01").build();
            taskRegistry.put(taskId, task);
        }

        String oldStatus = task.getStatus();
        String newStatus = body.getStatus(); // ACKNOWLEDGED, EN_ROUTE, ON_SITE, COMPLETED, UNABLE_TO_REACH, REASSIGNED, CANCELLED
        String notes = body.getNotes() != null ? body.getNotes() : "Status updated to " + newStatus;

        task.setStatus(newStatus);
        if (body.getFieldVerificationResult() != null) {
            task.setFieldVerificationResult(body.getFieldVerificationResult()); // HAZARD_CONFIRMED, NO_HAZARD_FOUND, CONDITION_CHANGED, UNABLE_TO_ASSESS
        }

        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "FIELD_OFFICER",
                actor != null ? actor.getRole().name() : "FIELD_OFFICER",
                "FIELD_TASK_STATUS_UPDATED",
                "FieldTask",
                taskId,
                oldStatus,
                newStatus,
                notes + (body.getFieldVerificationResult() != null ? " [Verification Result: " + body.getFieldVerificationResult() + "]" : ""),
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "status", newStatus,
                "fieldVerificationResult", body.getFieldVerificationResult() != null ? body.getFieldVerificationResult() : "PENDING",
                "updatedBy", actor != null ? actor.getUsername() : "FIELD_OFFICER",
                "updatedAt", LocalDateTime.now().toString(),
                "notes", notes
        ));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldInspectionTaskDto {
        private String taskId;
        private String title;
        private String districtName;
        private String assignedOfficer;
        private double targetLatitude;
        private double targetLongitude;
        private String status;                   // CREATED, RECEIVED, ACKNOWLEDGED, EN_ROUTE, ON_SITE, COMPLETED, UNABLE_TO_REACH, REASSIGNED, CANCELLED
        private String fieldVerificationResult; // HAZARD_CONFIRMED, NO_HAZARD_FOUND, CONDITION_CHANGED, UNABLE_TO_ASSESS
        private String priority;
        private String instructions;
        private String assignedAt;
    }

    @Data
    public static class TaskStatusUpdateDto {
        private String status;
        private String fieldVerificationResult; // HAZARD_CONFIRMED, NO_HAZARD_FOUND, CONDITION_CHANGED, UNABLE_TO_ASSESS
        private String notes;
    }
}
