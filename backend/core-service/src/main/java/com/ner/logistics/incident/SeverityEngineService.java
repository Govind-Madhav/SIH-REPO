package com.ner.logistics.incident;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeverityEngineService {

    private final IncidentRepository incidentRepository;

    public SeverityRecommendationResult calculateSeverityAndConfidence(CreateIncidentDto dto) {
        int score = 0;

        // 1. Incident Type Base Weight
        String type = dto.getType().toUpperCase();
        switch (type) {
            case "BRIDGE_DAMAGE": score += 40; break;
            case "LANDSLIDE":
            case "ROAD_BLOCKED": score += 35; break;
            case "FLOOD": score += 30; break;
            case "ROAD_DAMAGE": score += 20; break;
            case "HEAVY_RAIN": score += 15; break;
            default: score += 10; break;
        }

        // 2. Weather Condition Weight
        double rainfall = dto.getRainfallMm24h() != null ? dto.getRainfallMm24h() : 20.0;
        if (rainfall > 100.0) {
            score += 25;
        } else if (rainfall > 50.0) {
            score += 15;
        }

        // 3. Historical Risk Corridor Weight (Haflong Pass / Dima Hasao)
        if (dto.getLatitude() >= 24.8 && dto.getLatitude() <= 25.6 && dto.getLongitude() >= 92.2 && dto.getLongitude() <= 93.2) {
            score += 15;
        }

        // 4. Geographic Cluster Analysis (Duplicate reports within 2 km radius)
        List<Incident> nearbyReports = incidentRepository.findIncidentsNearLocation(dto.getLatitude(), dto.getLongitude(), 2000.0);
        int clusterCount = (int) nearbyReports.stream()
                .filter(i -> i.getType().equalsIgnoreCase(type))
                .count();

        if (clusterCount > 0) {
            score += Math.min(20, clusterCount * 10);
        }

        score = Math.min(100, Math.max(0, score));

        // 5. Severity Categorization
        String recommendedSeverity;
        if (score >= 81) {
            recommendedSeverity = "CRITICAL";
        } else if (score >= 61) {
            recommendedSeverity = "HIGH";
        } else if (score >= 31) {
            recommendedSeverity = "MEDIUM";
        } else {
            recommendedSeverity = "LOW";
        }

        // 6. Confidence Level Calculation
        double baseConfidence = 50.0;
        if (dto.getPhotoUrls() != null && !dto.getPhotoUrls().isEmpty()) {
            baseConfidence += 25.0; // Photo evidence present
        }
        baseConfidence += Math.min(25.0, clusterCount * 12.5); // Multiple reports boost confidence

        return new SeverityRecommendationResult(recommendedSeverity, score, Math.min(100.0, baseConfidence));
    }

    public record SeverityRecommendationResult(String recommendedSeverity, int severityScore, double confidenceLevel) {}
}
