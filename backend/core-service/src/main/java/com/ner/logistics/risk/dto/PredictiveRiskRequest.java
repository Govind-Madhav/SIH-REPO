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
public class PredictiveRiskRequest {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Integer predictionWindowHours;
}
