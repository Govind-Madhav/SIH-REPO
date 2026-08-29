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

    private Double primaryDistanceKm;

    private Integer primaryEtaMinutes;

    private String primaryRiskLevel;

    private List<RoutePoint> primaryWaypoints;

    private Double alternativeDistanceKm;

    private Integer alternativeEtaMinutes;

    private String alternativeRiskLevel;

    private List<RoutePoint> alternativeWaypoints;
}
