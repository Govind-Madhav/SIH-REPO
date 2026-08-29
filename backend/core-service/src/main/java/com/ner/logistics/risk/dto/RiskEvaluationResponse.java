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
public class RiskEvaluationResponse {

    private Integer currentRiskScore; // 0 to 100

    private String currentRiskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    private String assessmentType = "RULE_BASED_REAL_TIME";

    private List<FactorImpactDto> factors;

    private String explanation;
}
