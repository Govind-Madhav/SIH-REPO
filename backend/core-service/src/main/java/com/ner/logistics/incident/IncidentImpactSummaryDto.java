package com.ner.logistics.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentImpactSummaryDto {

    private Long incidentId;

    private String incidentType;

    private String districtName;

    private String reportedSeverity;

    private String recommendedSeverity;

    private Integer severityScore;

    private Double confidenceLevel;

    private Integer affectedVehiclesCount;

    private List<String> affectedVehicleCodes;

    private Integer affectedShipmentsCount;

    private List<String> affectedCommodities;

    private String verificationStatus;
}
