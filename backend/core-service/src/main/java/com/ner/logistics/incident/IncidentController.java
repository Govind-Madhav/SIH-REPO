package com.ner.logistics.incident;

import com.ner.logistics.file.FileUploadController;
import com.ner.logistics.file.FileUploadResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;
    private final FileUploadController fileUploadController;

    @PostMapping
    @PreAuthorize("hasAuthority('INCIDENT_REPORT') or hasAuthority('INCIDENT_VIEW')")
    public ResponseEntity<Incident> createIncident(@Valid @RequestBody CreateIncidentDto dto, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "FIELD_OFFICER";
        Incident incident = incidentService.createIncident(dto, username);
        return ResponseEntity.ok(incident);
    }

    @PostMapping("/{id}/evidence")
    @PreAuthorize("hasAuthority('INCIDENT_REPORT') or hasAuthority('INCIDENT_VERIFY')")
    public ResponseEntity<?> uploadIncidentEvidence(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        ResponseEntity<?> fileResp = fileUploadController.uploadFile(file);
        if (!fileResp.getStatusCode().is2xxSuccessful()) {
            return fileResp;
        }

        FileUploadResponseDto uploadDto = (FileUploadResponseDto) fileResp.getBody();
        if (uploadDto != null && uploadDto.getFileUrl() != null) {
            incidentService.attachPhotoEvidence(id, uploadDto.getFileUrl());
        }

        return ResponseEntity.ok(uploadDto);
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
    @PreAuthorize("hasAuthority('INCIDENT_REPORT') or hasAuthority('INCIDENT_VIEW')")
    public ResponseEntity<List<Incident>> syncOfflineIncidents(@RequestBody List<CreateIncidentDto> dtos, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "FIELD_OFFICER";
        List<Incident> synced = incidentService.syncOfflineIncidents(dtos, username);
        return ResponseEntity.ok(synced);
    }

    @PutMapping("/{id}/lifecycle")
    @PreAuthorize("hasAuthority('INCIDENT_VERIFY') or hasAuthority('INCIDENT_RESOLVE')")
    public ResponseEntity<Incident> updateLifecycle(@PathVariable Long id, @RequestParam String verificationStatus) {
        return ResponseEntity.ok(incidentService.updateLifecycleStatus(id, verificationStatus));
    }
}
