package com.ner.logistics.accessibility;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accessibility")
@RequiredArgsConstructor
public class AccessibilityController {

    private final AccessibilityEngineService accessibilityEngineService;
    private final DistrictAccessibilityService districtAccessibilityService;

    @PostMapping("/report")
    @PreAuthorize("hasAuthority('ROAD_STATUS_UPDATE') or hasAuthority('INCIDENT_REPORT') or hasAuthority('ROAD_STATUS_VIEW')")
    public ResponseEntity<Corridor> submitAccessibilityReport(
            @Valid @RequestBody AccessibilityReportDto dto,
            Authentication authentication) {

        String username = authentication != null ? authentication.getName() : "FIELD_OFFICER";
        Corridor updated = accessibilityEngineService.processAccessibilityReport(dto, username);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/corridors")
    public ResponseEntity<List<CorridorStatusDto>> getCorridors() {
        return ResponseEntity.ok(accessibilityEngineService.evaluateCorridors());
    }

    @GetMapping("/districts/heatmap")
    public ResponseEntity<List<DistrictHeatmapDto>> getDistrictHeatmap() {
        return ResponseEntity.ok(accessibilityEngineService.getDistrictHeatmap());
    }

    @GetMapping("/districts")
    public ResponseEntity<List<DistrictAccessibilityDto>> getDistrictAccessibility() {
        return ResponseEntity.ok(districtAccessibilityService.evaluateDistrictAccessibility());
    }
}
