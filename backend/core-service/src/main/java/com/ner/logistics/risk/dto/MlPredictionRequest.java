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
public class MlPredictionRequest {

    private Double latitude;

    private Double longitude;

    private Integer predictionWindowHours;

    private Map<String, Object> features;
}
