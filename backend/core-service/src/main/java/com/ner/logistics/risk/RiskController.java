package com.ner.logistics.risk;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskEngineService riskEngineService;

    @PostMapping("/evaluate")
    public ResponseEntity<RiskResponseDto> evaluateRisk(@Valid @RequestBody RiskRequestDto request) {
        return ResponseEntity.ok(riskEngineService.evaluateRisk(request));
    }

    @GetMapping("/location")
    public ResponseEntity<RiskResponseDto> getRiskByLocation(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false, defaultValue = "30.0") double rainfall) {
        RiskRequestDto dto = RiskRequestDto.builder()
                .latitude(lat)
                .longitude(lng)
                .rainfallMm24h(rainfall)
                .build();
        return ResponseEntity.ok(riskEngineService.evaluateRisk(dto));
    }

    @GetMapping("/predictive-timeline")
    public ResponseEntity<RiskPredictiveTimelineDto> getPredictiveTimeline() {
        return ResponseEntity.ok(riskEngineService.getPredictiveTimeline());
    }
}
