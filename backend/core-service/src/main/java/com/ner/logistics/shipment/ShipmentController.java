package com.ner.logistics.shipment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final SupplyGapAnalysisService supplyGapAnalysisService;

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        return ResponseEntity.ok(shipmentRepository.findAll());
    }

    @GetMapping("/supply-gaps")
    public ResponseEntity<List<SupplyGapDto>> getSupplyGaps() {
        return ResponseEntity.ok(supplyGapAnalysisService.analyzeSupplyGaps());
    }
}
