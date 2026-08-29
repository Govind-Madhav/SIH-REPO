package com.ner.logistics.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HybridRiskIntelligenceResponse {

    private Map<String, Double> location;

    private RiskEvaluationResponse currentAssessment;

    private MlPredictionResponse futurePrediction;
}
