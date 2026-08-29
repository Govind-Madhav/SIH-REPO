package com.ner.logistics.decision;

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
public class LogisticsDecisionService {

    private final IncidentRepository incidentRepository;

    public List<DecisionRecommendationDto> getRecommendations() {
        List<Incident> activeIncidents = incidentRepository.findByStatus("ACTIVE");
        List<DecisionRecommendationDto> recommendations = new ArrayList<>();

        if (!activeIncidents.isEmpty()) {
            recommendations.add(DecisionRecommendationDto.builder()
                    .decisionType("REROUTE_VEHICLE")
                    .priority("CRITICAL")
                    .targetEntity("Vehicle NER-07 (Medical Convoy)")
                    .recommendedAction("Reroute NER-07 immediately via Haflong Bypass Corridor (132 km - Low Risk)")
                    .destinationDistrict("Silchar Civil Hospital")
                    .rationale("Primary NH-27 Corridor blocked by active landslide debris at Haflong Pass")
                    .build());

            recommendations.add(DecisionRecommendationDto.builder()
                    .decisionType("HOLD_SHIPMENT")
                    .priority("HIGH")
                    .targetEntity("Fuel Tanker NER-04")
                    .recommendedAction("Hold NER-04 at nearest safe checkpoint (Umrangso Staging Area)")
                    .destinationDistrict("Haflong Power Substation")
                    .rationale("Hazardous fuel cargo should not enter active landslide zone until clearance")
                    .build());

            recommendations.add(DecisionRecommendationDto.builder()
                    .decisionType("DISPATCH_OFFICER")
                    .priority("HIGH")
                    .targetEntity("Haflong Sector Field Unit")
                    .recommendedAction("Dispatch Field Officer to Haflong Pass for visual verification & geotagged reporting")
                    .destinationDistrict("Dima Hasao")
                    .rationale("Confirm road clearance timeline and assess secondary rockfall risk")
                    .build());

            recommendations.add(DecisionRecommendationDto.builder()
                    .decisionType("MARK_CORRIDOR_INACCESSIBLE")
                    .priority("CRITICAL")
                    .targetEntity("NH-27 Guwahati -> Silchar Corridor")
                    .recommendedAction("Temporarily update corridor status to BLOCKED in regional logistics dispatch network")
                    .destinationDistrict("Dima Hasao / Cachar Corridor")
                    .rationale("Prevent secondary convoys from entering blocked mountain pass")
                    .build());
        } else {
            recommendations.add(DecisionRecommendationDto.builder()
                    .decisionType("MONITOR")
                    .priority("NORMAL")
                    .targetEntity("All Active Convoys (NER-01 to NER-08)")
                    .recommendedAction("Maintain scheduled transit speeds on primary corridors")
                    .destinationDistrict("Regional Network")
                    .rationale("No active disruptions detected on primary logistics corridors")
                    .build());
        }

        return recommendations;
    }
}
