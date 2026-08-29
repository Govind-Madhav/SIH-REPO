package com.ner.logistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    private Double districtAccessiblePct;
    private Long activeVehiclesCount;
    private Long activeIncidentsCount;
    private Integer highRiskCorridorsCount;
    private Integer delayedShipmentsCount;
    private String overallRiskLevel;
}
