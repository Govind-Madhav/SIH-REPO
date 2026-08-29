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
public class RiskPredictiveTimelineDto {
    private String currentRiskStatus;
    private String predictedNext2HoursRiskStatus;
    private Double probabilityPct;
    private List<String> predictiveFactors;
    private List<RiskTimelinePointDto> timelineHistory;
}
