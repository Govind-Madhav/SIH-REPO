package com.ner.logistics.shipment;

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
public class SupplyGapAnalysisService {

    private final ShipmentRepository shipmentRepository;
    private final IncidentRepository incidentRepository;

    public List<SupplyGapDto> analyzeSupplyGaps() {
        List<Incident> activeIncidents = incidentRepository.findByStatus("ACTIVE");
        List<SupplyGapDto> gaps = new ArrayList<>();

        if (!activeIncidents.isEmpty()) {
            Incident inc = activeIncidents.get(0);
            String district = "Dima Hasao";

            // Gap 1: Medical Oxygen Supply
            gaps.add(SupplyGapDto.builder()
                    .district(district)
                    .commodityType("OXYGEN_CYLINDERS")
                    .riskLevel("CRITICAL")
                    .estimatedDelayHours(4)
                    .affectedShipmentsCount(1)
                    .recommendedAction("PRIORITIZE_ALTERNATIVE_SUPPLY")
                    .rationale("Medical convoy NER-07 carrying medical oxygen delayed at Haflong Pass due to active "
                            + inc.getType())
                    .build());

            // Gap 2: Essential Medicines
            gaps.add(SupplyGapDto.builder()
                    .district(district)
                    .commodityType("ESSENTIAL_MEDICINE")
                    .riskLevel("HIGH")
                    .estimatedDelayHours(3)
                    .affectedShipmentsCount(1)
                    .recommendedAction("REROUTE_CONVOY_HAFLONG_BYPASS")
                    .rationale(
                            "Silchar District Hospital inventory projected below 24h buffer threshold if delay exceeds 3.5h")
                    .build());

            // Gap 3: Emergency Fuel Reserve
            gaps.add(SupplyGapDto.builder()
                    .district("Cachar (Silchar)")
                    .commodityType("DIESEL_FUEL")
                    .riskLevel("MEDIUM")
                    .estimatedDelayHours(2)
                    .affectedShipmentsCount(1)
                    .recommendedAction("STAGGER_REGIONAL_STATION_DISPATCH")
                    .rationale("Fuel tanker NER-04 held at Umrangso checkpoint until road clearance verification")
                    .build());
        } else {
            gaps.add(SupplyGapDto.builder()
                    .district("All NER Districts")
                    .commodityType("ALL_COMMODITIES")
                    .riskLevel("LOW")
                    .estimatedDelayHours(0)
                    .affectedShipmentsCount(0)
                    .recommendedAction("MAINTAIN_STANDARD_SCHEDULE")
                    .rationale("No active supply chain bottlenecks or commodity shipment disruptions detected.")
                    .build());
        }

        return gaps;
    }
}
