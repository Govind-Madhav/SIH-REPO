package com.ner.logistics.risk.service;

import com.ner.logistics.incident.Incident;
import com.ner.logistics.incident.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiskFeatureService {

    private final IncidentRepository incidentRepository;

    public Map<String, Object> collectFeatureVector(Double latitude, Double longitude, Double rainfallMm24h) {
        Map<String, Object> features = new HashMap<>();

        double rainfall24h = rainfallMm24h != null ? rainfallMm24h : 25.0;
        double rainfall6h = rainfall24h * 0.45;
        double rainfall1h = rainfall24h * 0.12;

        features.put("rainfall1h", rainfall1h);
        features.put("rainfall6h", rainfall6h);
        features.put("rainfall24h", rainfall24h);
        features.put("rainfallTrend", rainfall24h > 60.0 ? "INCREASING" : "STABLE");
        features.put("temperature", 22.5);
        features.put("humidity", 88.0);
        features.put("soilMoisture", rainfall24h > 80.0 ? 84.5 : 45.0);
        features.put("slopeDegrees", 34.2);
        features.put("elevation", 980.0);
        features.put("terrainRuggedness", 0.78);

        // Spatial incident density within 10 km
        List<Incident> nearbyIncidents = incidentRepository.findIncidentsNearLocation(latitude, longitude, 10000.0);
        int incidentCount = nearbyIncidents.size();
        features.put("incidentDensity", incidentCount);

        // Historical disaster frequencies
        boolean inHaflongPass = (latitude >= 24.8 && latitude <= 25.6 && longitude >= 92.2 && longitude <= 93.2);
        features.put("historicalLandslideCount", inHaflongPass ? 18 : 3);
        features.put("historicalFloodCount", inHaflongPass ? 6 : 1);
        features.put("historicalRoadBlockCount", inHaflongPass ? 12 : 2);

        return features;
    }
}
