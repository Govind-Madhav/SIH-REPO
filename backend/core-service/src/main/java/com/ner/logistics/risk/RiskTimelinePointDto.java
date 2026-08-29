package com.ner.logistics.risk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskTimelinePointDto {
    private String timeLabel; // e.g. "10:00 AM"
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private Integer riskScore; // 0 to 100
    private String primaryReason; // e.g. "Rainfall increased to 120mm"
}
