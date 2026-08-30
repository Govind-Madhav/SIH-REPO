package com.ner.logistics.routing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponseDto {

    private String vehicleCode;

    private Boolean isRerouteRecommended;

    private String rerouteReason;

    private String recommendationAction; // REROUTE, PROCEED_PRIMARY, HOLD

    private Integer estimatedDelayMinutes;

    private String riskReduction; // HIGH, MEDIUM, LOW, NONE

    private Integer affectedIncidentsCount;

    private Double primaryDistanceKm;

    private Integer primaryEtaMinutes;

    private String primaryRiskLevel;

    private List<RoutePoint> primaryWaypoints;

    private Double alternativeDistanceKm;

    private Integer alternativeEtaMinutes;

    private String alternativeRiskLevel;

    private List<RoutePoint> alternativeWaypoints;
}
