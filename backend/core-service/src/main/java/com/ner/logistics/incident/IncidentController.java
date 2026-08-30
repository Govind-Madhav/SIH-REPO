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
    public ResponseEntity<List<Incident>> getActiveIncidents(@RequestParam(required = false) String severity) {
        return ResponseEntity.ok(incidentService.getActiveIncidents(severity));
    }

    @GetMapping("/{id}/impact")
    public ResponseEntity<IncidentImpactSummaryDto> getIncidentImpact(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.analyzeImpact(id));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Incident>> getNearbyIncidents(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10000") double distanceMeters) {
        return ResponseEntity.ok(incidentService.getNearbyIncidents(lat, lng, distanceMeters));
    }

    @PostMapping("/sync")
    public ResponseEntity<List<Incident>> syncOfflineIncidents(@RequestBody List<CreateIncidentDto> dtos, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "FIELD_OFFICER";
        List<Incident> synced = incidentService.syncOfflineIncidents(dtos, username);
        return ResponseEntity.ok(synced);
    }

    @PutMapping("/{id}/lifecycle")
    public ResponseEntity<Incident> updateLifecycle(@PathVariable Long id, @RequestParam String verificationStatus) {
        return ResponseEntity.ok(incidentService.updateLifecycleStatus(id, verificationStatus));
    }
}

