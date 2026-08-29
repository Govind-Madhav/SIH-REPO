package com.ner.logistics.accessibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorridorStatusDto {

    private String corridorName;

    private String corridorCode;

    private Double accessibilityScorePct;

    private String status; // ACCESSIBLE, DEGRADED, HIGH_RISK, BLOCKED

    private List<String> reasons;
}
