package com.ner.logistics.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MlPredictionResponse {

    private boolean available;

    private String modelName; // XGBoost, LightGBM

    private String modelVersion; // 1.0

    private String predictionWindow; // NEXT_2_HOURS

    private Double disruptionProbability; // 0.00 to 1.00

    private String predictedRiskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    private List<FactorImpactDto> topFactors;

    private String generatedAt;

    private String message;
}
