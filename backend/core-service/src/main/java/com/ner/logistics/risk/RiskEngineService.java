package com.ner.logistics.risk;

import com.ner.logistics.incident.Incident;
import com.ner.logistics.incident.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskEngineService {

    private final IncidentRepository incidentRepository;

    public RiskResponseDto evaluateRisk(RiskRequestDto request) {
        int score = 0;
        List<String> factors = new ArrayList<>();

        double rainfall = request.getRainfallMm24h() != null ? request.getRainfallMm24h() : 25.0;
        double weatherImpactPct = 20.0;
        double roadConditionPct = 20.0;
        double historicalRiskPct = 35.0;

        // 1. Weather Factor Calculation
        if (rainfall > 120.0) {
            score += 35;
            weatherImpactPct = 85.0;
            factors.add(String.format("Torrential rainfall warning (%.1f mm/24h) detected in sector", rainfall));
        } else if (rainfall > 60.0) {
            score += 20;
            weatherImpactPct = 55.0;
            factors.add(String.format("Moderate heavy rainfall (%.1f mm/24h) in sector", rainfall));
        } else {
            factors.add("Normal precipitation levels");
        }

        // 2. PostGIS Spatial Incident Proximity Search (10km radius)
        List<Incident> nearbyIncidents = incidentRepository.findIncidentsNearLocation(request.getLatitude(), request.getLongitude(), 10000.0);
        if (!nearbyIncidents.isEmpty()) {
            boolean hasCritical = nearbyIncidents.stream().anyMatch(i -> 
                "CRITICAL".equalsIgnoreCase(i.getReportedSeverity()) || 
                "HIGH".equalsIgnoreCase(i.getReportedSeverity()) ||
                "CRITICAL".equalsIgnoreCase(i.getRecommendedSeverity()) ||
                "HIGH".equalsIgnoreCase(i.getRecommendedSeverity())
            );
            if (hasCritical) {
                score += 45;
                roadConditionPct = 90.0;
                factors.add(String.format("🚨 Critical/High incident reported within 10km radius (%d active incidents)", nearbyIncidents.size()));
            } else {
                score += 25;
                roadConditionPct = 50.0;
                factors.add(String.format("Active incidents reported within 10km radius (%d active)", nearbyIncidents.size()));
            }
        }

        // 3. Road Condition Factor
        if ("SEVERELY_DAMAGED".equalsIgnoreCase(request.getRoadCondition())) {
            score += 20;
            factors.add("Severe pavement degradation and landslide debris on corridor");
        } else if ("DEGRADED".equalsIgnoreCase(request.getRoadCondition())) {
            score += 10;
            factors.add("Corridor surface degraded");
        }

        // 4. Historical Mountain Corridor Risk Factor (Haflong / Dima Hasao Sector)
        if (request.getLatitude() >= 24.8 && request.getLatitude() <= 25.6 && request.getLongitude() >= 92.2 && request.getLongitude() <= 93.2) {
            score += 15;
            historicalRiskPct = 75.0;
            factors.add("High historical landslide frequency zone (Dima Hasao Corridor)");
        }

        score = Math.min(100, Math.max(0, score));

        String riskLevel;
        if (score >= 75) {
            riskLevel = "CRITICAL";
        } else if (score >= 50) {
            riskLevel = "HIGH";
        } else if (score >= 25) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "LOW";
        }

        return RiskResponseDto.builder()
                .riskLevel(riskLevel)
                .riskScore(score)
                .weatherImpactPct(weatherImpactPct)
                .roadConditionPct(roadConditionPct)
                .historicalRiskPct(historicalRiskPct)
                .factors(factors)
                .build();
    }
}
