package com.ner.logistics.risk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationRequest {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Double rainfallMm24h;

    private String roadCondition; // GOOD, DEGRADED, SEVERELY_DAMAGED
}
