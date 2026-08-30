package com.ner.logistics.emergency;

import com.ner.logistics.audit.AuditService;
import com.ner.logistics.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emergency/resources")
@RequiredArgsConstructor
public class EmergencyResourceController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('EMERGENCY_RESOURCE_MANAGE') or hasAuthority('SOS_VIEW')")
    public ResponseEntity<List<EmergencyResourceDto>> getAllResources() {
        List<EmergencyResourceDto> resources = List.of(
                EmergencyResourceDto.builder()
                        .resourceId("RES-TEAM-HAFLONG-01")
                        .name("Haflong NDRF Mountain Rescue Team A")
                        .resourceType("RESCUE_TEAM")
                        .status("AVAILABLE")
                        .baseLocation("Haflong Sector HQ")
                        .contactPhone("+919876500111")
                        .build(),
                EmergencyResourceDto.builder()
                        .resourceId("RES-AMB-SILCHAR-04")
                        .name("Silchar Medical 4WD ICU Ambulance")
                        .resourceType("AMBULANCE")
                        .status("AVAILABLE")
                        .baseLocation("Silchar Medical College Hospital")
                        .contactPhone("+919876500222")
                        .build(),
                EmergencyResourceDto.builder()
                        .resourceId("RES-EXCAVATOR-02")
                        .name("PWD Heavy Earthmover & Debris Clearance Unit")
                        .resourceType("HEAVY_EQUIPMENT")
                        .status("EN_ROUTE")
                        .baseLocation("Umrangso Depot")
                        .contactPhone("+919876500333")
                        .build()
        );
        return ResponseEntity.ok(resources);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMERGENCY_RESOURCE_MANAGE')")
    public ResponseEntity<EmergencyResourceDto> createResource(@RequestBody EmergencyResourceDto dto,
                                                                @AuthenticationPrincipal User actor) {
        auditService.logDetailedEvent(
                actor != null ? actor.getUsername() : "ADMIN",
                actor != null ? actor.getRole().name() : "ADMIN",
                "EMERGENCY_RESOURCE_CREATED",
                "EmergencyResource",
                dto.getResourceId(),
                null,
                dto.getStatus(),
                "Registered emergency rescue resource " + dto.getName(),
                null,
                "SUCCESS"
        );
        return ResponseEntity.ok(dto);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyResourceDto {
        private String resourceId;
        private String name;
        private String resourceType; // RESCUE_TEAM, AMBULANCE, HEAVY_EQUIPMENT, PERSONNEL
        private String status;       // AVAILABLE, ASSIGNED, EN_ROUTE, BUSY, UNAVAILABLE
        private String baseLocation;
        private String contactPhone;
    }
}
