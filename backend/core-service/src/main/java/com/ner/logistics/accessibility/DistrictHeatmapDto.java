package com.ner.logistics.accessibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictHeatmapDto {

    private String districtName;

    private String districtCode;

    private Double accessibilityScorePct;

    private String statusCategory; // ACCESSIBLE (80-100%), PARTIALLY_ACCESSIBLE (60-79%), SEVERELY_AFFECTED (30-59%), CRITICAL (0-29%)

    private String mapColorHex; // #22c55e (Green), #eab308 (Yellow), #f97316 (Orange), #ef4444 (Red)
}
