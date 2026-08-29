package com.ner.logistics.risk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskResponseDto {

    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    private Integer riskScore; // 0 to 100

    private Double weatherImpactPct;

    private Double roadConditionPct;

    private Double historicalRiskPct;

    private List<String> factors; // Natural language explainability strings
}
