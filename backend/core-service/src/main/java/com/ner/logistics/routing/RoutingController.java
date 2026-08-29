package com.ner.logistics.routing;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RoutingController {

    private final GraphHopperRoutingService routingService;

    @PostMapping("/calculate")
    public ResponseEntity<RouteResponseDto> calculateRoute(@Valid @RequestBody RouteRequestDto request) {
        return ResponseEntity.ok(routingService.calculateRoute(request));
    }

    @PostMapping("/reroute")
    public ResponseEntity<RouteResponseDto> rerouteVehicle(@RequestParam String vehicleCode) {
        return ResponseEntity.ok(routingService.rerouteVehicle(vehicleCode));
    }
}
