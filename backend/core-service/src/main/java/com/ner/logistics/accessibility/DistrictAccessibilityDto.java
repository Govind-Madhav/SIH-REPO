package com.ner.logistics.accessibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictAccessibilityDto {

    private String district;

    private String districtCode;

    private Double roadAccessibilityPct;

    private Integer activeIncidentsCount;

    private String weatherRisk; // LOW, MEDIUM, HIGH, CRITICAL

    private Boolean transportAvailability;

    private Boolean routeAvailability;

    private Double overallAccessibilityScore;

    private String status; // ACCESSIBLE, PARTIALLY_ACCESSIBLE, SEVERELY_RESTRICTED, INACCESSIBLE
}
