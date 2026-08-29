package com.ner.logistics.risk.controller;

import com.ner.logistics.risk.dto.*;
import com.ner.logistics.risk.service.MlPredictionService;
import com.ner.logistics.risk.service.PredictiveRiskService;
import com.ner.logistics.risk.service.RiskEngineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskEngineService riskEngineService;
    private final MlPredictionService mlPredictionService;
    private final PredictiveRiskService predictiveRiskService;

    @PostMapping("/evaluate")
    public ResponseEntity<RiskEvaluationResponse> evaluateRisk(@Valid @RequestBody RiskEvaluationRequest request) {
        return ResponseEntity.ok(riskEngineService.evaluateRealTimeRisk(request));
    }

    @PostMapping("/predict")
    public ResponseEntity<MlPredictionResponse> predictRisk(@Valid @RequestBody PredictiveRiskRequest request) {
        return ResponseEntity.ok(mlPredictionService.getPrediction(request));
    }

    @GetMapping("/intelligence")
    public ResponseEntity<HybridRiskIntelligenceResponse> getHybridIntelligence(
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(predictiveRiskService.getHybridIntelligence(lat, lng));
    }

    @GetMapping("/location")
    public ResponseEntity<RiskEvaluationResponse> getRiskByLocation(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false, defaultValue = "30.0") double rainfall) {
        RiskEvaluationRequest req = RiskEvaluationRequest.builder()
                .latitude(lat)
                .longitude(lng)
                .rainfallMm24h(rainfall)
                .build();
        return ResponseEntity.ok(riskEngineService.evaluateRealTimeRisk(req));
    }
}
