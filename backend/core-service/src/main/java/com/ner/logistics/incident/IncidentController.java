package com.ner.logistics.incident;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<Incident> createIncident(@Valid @RequestBody CreateIncidentDto dto, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "FIELD_OFFICER";
        Incident incident = incidentService.createIncident(dto, username);
        return ResponseEntity.ok(incident);
    }

    @GetMapping
    public ResponseEntity<List<Incident>> getActiveIncidents() {
        return ResponseEntity.ok(incidentService.getActiveIncidents());
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Incident>> getNearbyIncidents(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10000") double distanceMeters) {
        return ResponseEntity.ok(incidentService.getNearbyIncidents(lat, lng, distanceMeters));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Incident> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(incidentService.updateIncidentStatus(id, status));
    }
}
