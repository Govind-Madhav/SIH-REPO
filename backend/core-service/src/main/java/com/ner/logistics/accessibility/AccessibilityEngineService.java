package com.ner.logistics.accessibility;

import com.ner.logistics.incident.Incident;
import com.ner.logistics.incident.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessibilityEngineService {

    private final CorridorRepository corridorRepository;
    private final DistrictRepository districtRepository;
    private final IncidentRepository incidentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Corridor processAccessibilityReport(AccessibilityReportDto dto, String username) {
        String code = dto.getCorridorCode() != null ? dto.getCorridorCode() : "COR-NH27";

        Corridor corridor = corridorRepository.findByCode(code)
                .orElseGet(() -> corridorRepository.findAll().stream().findFirst().orElse(null));

        if (corridor != null) {
            String newStatus = dto.getStatus().toUpperCase();
            corridor.setStatus(newStatus);

            if ("BLOCKED".equalsIgnoreCase(newStatus)) {
                corridor.setAccessibilityScorePct(15.0);
            } else if ("RESTRICTED".equalsIgnoreCase(newStatus) || "PARTIALLY_ACCESSIBLE".equalsIgnoreCase(newStatus)) {
                corridor.setAccessibilityScorePct(55.0);
            } else {
                corridor.setAccessibilityScorePct(95.0);
            }

            Corridor saved = corridorRepository.save(corridor);
            log.info("🛣️ Field Officer Accessibility Report: Corridor {} updated to status {} by officer {}", code, newStatus, username);

            // Feed into Intelligence Pipeline: Broadcast WebSocket alert to trigger route recalculations
            messagingTemplate.convertAndSend("/topic/route-updates", evaluateCorridors());

            return saved;
        }

        return null;
    }

    public List<CorridorStatusDto> evaluateCorridors() {
        List<Incident> activeIncidents = incidentRepository.findByStatus("ACTIVE");
        List<Corridor> corridors = corridorRepository.findAll();

        List<CorridorStatusDto> results = new ArrayList<>();

        for (Corridor c : corridors) {
            double score = 100.0;
            List<String> reasons = new ArrayList<>();

            if (!activeIncidents.isEmpty()) {
                score -= Math.min(60.0, activeIncidents.size() * 25.0);
                reasons.add(String.format("%d active disruption incident(s) reported along corridor network", activeIncidents.size()));
            }

            // Mountain Pass Sector Penalty
            if ("COR-NH27".equalsIgnoreCase(c.getCode())) {
                score -= 15.0;
                reasons.add("Torrential weather warning & high historical disruption frequency");
            } else {
                reasons.add("Normal traffic flow & weather conditions");
            }

            score = Math.max(0.0, score);
            String status;
            if (score >= 80) status = "ACCESSIBLE";
            else if (score >= 60) status = "DEGRADED";
            else if (score >= 30) status = "HIGH_RISK";
            else status = "BLOCKED";

            results.add(CorridorStatusDto.builder()
                    .corridorName(c.getName())
                    .corridorCode(c.getCode())
                    .accessibilityScorePct(score)
                    .status(status)
                    .reasons(reasons)
                    .build());
        }

        return results;
    }

    public List<DistrictHeatmapDto> getDistrictHeatmap() {
        List<District> districts = districtRepository.findAll();
        List<Incident> activeIncidents = incidentRepository.findByStatus("ACTIVE");

        return districts.stream().map(d -> {
            double score = d.getAccessibilityPct() != null ? d.getAccessibilityPct() : 75.0;

            if ("Dima Hasao".equalsIgnoreCase(d.getName()) && !activeIncidents.isEmpty()) {
                score = 28.5; // Critical due to Haflong Pass incidents
            }

            String category;
            String hex;
            if (score >= 80) {
                category = "ACCESSIBLE";
                hex = "#22c55e"; // Green
            } else if (score >= 60) {
                category = "PARTIALLY_ACCESSIBLE";
                hex = "#eab308"; // Yellow
            } else if (score >= 30) {
                category = "SEVERELY_AFFECTED";
                hex = "#f97316"; // Orange
            } else {
                category = "CRITICAL";
                hex = "#ef4444"; // Red
            }

            return DistrictHeatmapDto.builder()
                    .districtName(d.getName())
                    .districtCode(d.getCode())
                    .accessibilityScorePct(score)
                    .statusCategory(category)
                    .mapColorHex(hex)
                    .build();
        }).toList();
    }
}
