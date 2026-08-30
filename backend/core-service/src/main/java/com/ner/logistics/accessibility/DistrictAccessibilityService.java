package com.ner.logistics.accessibility;

import com.ner.logistics.incident.Incident;
import com.ner.logistics.incident.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictAccessibilityService {

    private final IncidentRepository incidentRepository;
    private final DistrictRepository districtRepository;

    public List<DistrictAccessibilityDto> evaluateDistrictAccessibility() {
        List<Incident> activeIncidents = incidentRepository.findByStatus("ACTIVE");
        List<District> districts = districtRepository.findAll();

        List<DistrictAccessibilityDto> list = new ArrayList<>();

        for (District d : districts) {
            long incidentCount = activeIncidents.stream()
                    .filter(i -> d.getName().equalsIgnoreCase(i.getDistrictName()))
                    .count();

            double baseScore = d.getAccessibilityPct() != null ? d.getAccessibilityPct() : 85.0;
            if (incidentCount > 0) {
                baseScore = Math.max(15.0, baseScore - (incidentCount * 25.0));
            }

            String weatherRisk = incidentCount > 1 ? "CRITICAL" : (incidentCount == 1 ? "HIGH" : "LOW");
            String status;
            if (baseScore >= 80) status = "ACCESSIBLE";
            else if (baseScore >= 60) status = "PARTIALLY_ACCESSIBLE";
            else if (baseScore >= 30) status = "SEVERELY_RESTRICTED";
            else status = "INACCESSIBLE";

            list.add(DistrictAccessibilityDto.builder()
                    .district(d.getName())
                    .districtCode(d.getCode())
                    .roadAccessibilityPct(baseScore)
                    .activeIncidentsCount((int) incidentCount)
                    .weatherRisk(weatherRisk)
                    .transportAvailability(baseScore >= 40)
                    .routeAvailability(baseScore >= 30)
                    .overallAccessibilityScore(baseScore)
                    .status(status)
                    .build());
        }

        // Fallback default districts if database is initial
        if (list.isEmpty()) {
            list.add(DistrictAccessibilityDto.builder()
                    .district("Dima Hasao")
                    .districtCode("DIST-DH")
                    .roadAccessibilityPct(28.5)
                    .activeIncidentsCount(activeIncidents.size())
                    .weatherRisk("HIGH")
                    .transportAvailability(true)
                    .routeAvailability(true)
                    .overallAccessibilityScore(28.5)
                    .status(activeIncidents.isEmpty() ? "ACCESSIBLE" : "SEVERELY_RESTRICTED")
                    .build());

            list.add(DistrictAccessibilityDto.builder()
                    .district("Cachar (Silchar)")
                    .districtCode("DIST-CA")
                    .roadAccessibilityPct(78.0)
                    .activeIncidentsCount(0)
                    .weatherRisk("MEDIUM")
                    .transportAvailability(true)
                    .routeAvailability(true)
                    .overallAccessibilityScore(78.0)
                    .status("PARTIALLY_ACCESSIBLE")
                    .build());
        }

        return list;
    }
}
