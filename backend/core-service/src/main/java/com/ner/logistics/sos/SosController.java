package com.ner.logistics.sos;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
public class SosController {

    private final SosService sosService;

    @PostMapping("/trigger")
    @PreAuthorize("hasAuthority('SOS_TRIGGER') or hasAuthority('SOS_VIEW')")
    public ResponseEntity<SosEvent> triggerSos(@Valid @RequestBody SosRequestDto dto, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "CONVOY_DRIVER";
        return ResponseEntity.ok(sosService.triggerSos(dto, username));
    }

    @PostMapping("/relay")
    public ResponseEntity<SosEvent> processRelayedSos(@Valid @RequestBody SosRelayRequestDto dto) {
        return ResponseEntity.ok(sosService.processRelayedSos(dto));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('SOS_VIEW') or hasAuthority('SOS_VIEW_SELF')")
    public ResponseEntity<List<SosEvent>> getActiveSosEvents() {
        return ResponseEntity.ok(sosService.getActiveSosEvents());
    }

    @GetMapping("/acks")
    public ResponseEntity<List<SosAck>> getActiveAcks() {
        return ResponseEntity.ok(sosService.getActiveAcks());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SOS_VIEW') or hasAuthority('SOS_VIEW_SELF')")
    public ResponseEntity<SosEvent> getSosById(@PathVariable Long id) {
        return ResponseEntity.ok(sosService.getSosById(id));
    }

    @PutMapping("/{id}/acknowledge")
    @PreAuthorize("hasAuthority('SOS_ACKNOWLEDGE')")
    public ResponseEntity<SosEvent> acknowledgeSos(@PathVariable Long id, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "CENTRAL_COMMAND";
        return ResponseEntity.ok(sosService.acknowledgeSos(id, username));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('SOS_DISPATCH')")
    public ResponseEntity<SosEvent> assignResponder(@PathVariable Long id, @RequestParam(required = false) String responderName) {
        return ResponseEntity.ok(sosService.assignResponder(id, responderName));
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAuthority('SOS_RESOLVE')")
    public ResponseEntity<SosEvent> resolveSos(@PathVariable Long id, @RequestParam(required = false) String resolutionNotes) {
        return ResponseEntity.ok(sosService.resolveSos(id, resolutionNotes));
    }
}
