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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/field-tasks")
@RequiredArgsConstructor
public class FieldTaskController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('FIELD_TASK_VIEW') or hasAuthority('INCIDENT_VIEW')")
    public ResponseEntity<List<FieldInspectionTaskDto>> getAssignedTasks(@AuthenticationPrincipal User actor) {
        String officerName = actor != null ? actor.getUsername() : "FIELD_OFFICER_01";
        List<FieldInspectionTaskDto> tasks = List.of(
                FieldInspectionTaskDto.builder()
                        .taskId("TASK-HAFLONG-104")
                        .title("Inspect NH-27 Landslide & Soil Saturation Anomaly")
                        .districtName("Dima Hasao")
                        .assignedOfficer(officerName)
                        .targetLatitude(25.1833)
                        .targetLongitude(92.8333)
                        .status("RECEIVED") // RECEIVED, ACKNOWLEDGED, EN_ROUTE, ON_SITE, COMPLETED
                        .priority("HIGH")
                        .instructions("IoT Sensor #SENS-SOIL-88 triggered 92% soil saturation alert. Confirm physical rockfall risk.")
                        .assignedAt(LocalDateTime.now().minusMinutes(30).toString())
                        .build()
        );
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FIELD_TASK_VIEW') or hasAuthority('INCIDENT_VERIFY')")
    public ResponseEntity<FieldInspectionTaskDto> createFieldTask(@RequestBody FieldInspectionTaskDto dto,
                                                                   @AuthenticationPrincipal User actor) {
        dto.setTaskId("TASK-" + System.currentTimeMillis() % 100000);
        dto.setStatus("RECEIVED");
        dto.setAssignedAt(LocalDateTime.now().toString());

        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "COMMAND_CENTER",
                actor != null ? actor.getRole().name() : "EMERGENCY_OPERATOR",
                "FIELD_TASK_ASSIGNED",
                "FieldTask",
                dto.getTaskId(),
                null,
                "RECEIVED",
                "Assigned field inspection task to officer " + dto.getAssignedOfficer(),
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasAuthority('FIELD_TASK_ACKNOWLEDGE') or hasAuthority('FIELD_TASK_UPDATE')")
    public ResponseEntity<?> updateTaskStatus(@PathVariable String taskId,
                                              @RequestBody Map<String, String> body,
                                              @AuthenticationPrincipal User actor) {
        String newStatus = body.get("status"); // ACKNOWLEDGED, EN_ROUTE, ON_SITE, COMPLETED
        String notes = body.getOrDefault("notes", "Status updated to " + newStatus);

        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "FIELD_OFFICER",
                actor != null ? actor.getRole().name() : "FIELD_OFFICER",
                "FIELD_TASK_STATUS_UPDATED",
                "FieldTask",
                taskId,
                "RECEIVED",
                newStatus,
                notes,
                null,
                "SUCCESS"
        );

        return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "status", newStatus,
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
        private String status;
        private String priority;
        private String instructions;
        private String assignedAt;
    }
}
