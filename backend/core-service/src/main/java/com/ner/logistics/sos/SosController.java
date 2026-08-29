package com.ner.logistics.sos;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
public class SosController {

    private final SosService sosService;

    @PostMapping("/trigger")
    public ResponseEntity<SosEvent> triggerSos(@Valid @RequestBody SosRequestDto dto, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "CONVOY_DRIVER";
        return ResponseEntity.ok(sosService.triggerSos(dto, username));
    }

    @GetMapping("/active")
    public ResponseEntity<List<SosEvent>> getActiveSosEvents() {
        return ResponseEntity.ok(sosService.getActiveSosEvents());
    }
}
