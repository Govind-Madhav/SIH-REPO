package com.ner.logistics.accessibility;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accessibility")
@RequiredArgsConstructor
public class AccessibilityController {

    private final AccessibilityEngineService accessibilityEngineService;
    private final DistrictAccessibilityService districtAccessibilityService;

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
