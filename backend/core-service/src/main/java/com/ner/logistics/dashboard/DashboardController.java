package com.ner.logistics.dashboard;

import com.ner.logistics.incident.IncidentRepository;
import com.ner.logistics.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final VehicleRepository vehicleRepository;
    private final IncidentRepository incidentRepository;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        long activeVehicles = vehicleRepository.count();
        long activeIncidents = incidentRepository.findByStatus("ACTIVE").size();

        String overallRisk = activeIncidents > 2 ? "CRITICAL" : activeIncidents > 0 ? "HIGH" : "LOW";

        DashboardSummaryDto summary = DashboardSummaryDto.builder()
                .districtAccessiblePct(68.5)
                .activeVehiclesCount(activeVehicles > 0 ? activeVehicles : 8L)
                .activeIncidentsCount(activeIncidents)
                .highRiskCorridorsCount(5)
                .delayedShipmentsCount(2)
                .overallRiskLevel(overallRisk)
                .build();

        return ResponseEntity.ok(summary);
    }
}
