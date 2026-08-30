package com.ner.logistics.shipment;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final SupplyGapAnalysisService supplyGapAnalysisService;
    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('SHIPMENT_VIEW') or hasAuthority('SHIPMENT_VIEW_SELF')")
    public ResponseEntity<List<Shipment>> getAllShipments() {
        return ResponseEntity.ok(shipmentRepository.findAll());
    }

    @GetMapping("/supply-gaps")
    @PreAuthorize("hasAuthority('SUPPLY_GAP_VIEW') or hasAuthority('SHIPMENT_VIEW') or hasAuthority('ANALYTICS_VIEW')")
    public ResponseEntity<List<SupplyGapDto>> getSupplyGaps() {
        return ResponseEntity.ok(supplyGapAnalysisService.analyzeSupplyGaps());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SHIPMENT_MANAGE')")
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        if (shipment.getStatus() == null) {
            shipment.setStatus("PLANNED");
        }
        return ResponseEntity.ok(shipmentRepository.save(shipment));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SHIPMENT_MANAGE') or hasAuthority('DELIVERY_STATUS_UPDATE')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> body,
                                          @AuthenticationPrincipal User actor) {
        String newStatus = body.get("status");
        String justificationReason = body.get("justificationReason");

        return shipmentRepository.findById(id).map(shipment -> {
            String oldStatus = shipment.getStatus();

            // Controlled State Transition Validation
            if ("DELIVERED".equals(oldStatus) && !"DELIVERED".equals(newStatus)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid state transition: Cannot revert DELIVERED shipment back to " + newStatus));
            }

            shipment.setStatus(newStatus);
            shipmentRepository.save(shipment);

            auditService.logDetailedEvent(
                    actor != null ? actor.getUsername() : "SYSTEM",
                    actor != null ? actor.getRole().name() : "LOGISTICS_OPERATOR",
                    "SHIPMENT_STATUS_UPDATED",
                    "Shipment",
                    id.toString(),
                    oldStatus,
                    newStatus,
                    justificationReason != null ? justificationReason : "Status updated to " + newStatus,
                    null,
                    "SUCCESS"
            );

            return ResponseEntity.ok(shipment);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/proof-of-delivery")
    @PreAuthorize("hasAuthority('DELIVERY_STATUS_UPDATE') or hasAuthority('SHIPMENT_MANAGE')")
    public ResponseEntity<?> submitProofOfDelivery(@PathVariable Long id, @RequestBody ProofOfDeliveryDto dto) {
        return shipmentRepository.findById(id).map(shipment -> {
            shipment.setReceiverOtp(dto.getReceiverOtp());
            shipment.setProofPhotoUrl(dto.getProofPhotoUrl());
            shipment.setStatus("DELIVERED");
            shipmentRepository.save(shipment);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Proof of delivery submitted successfully. Pending operator confirmation."));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/confirm-delivery")
    @PreAuthorize("hasAuthority('DELIVERY_CONFIRM') or hasAuthority('SHIPMENT_MANAGE')")
    public ResponseEntity<?> confirmDelivery(@PathVariable Long id,
                                              @RequestBody Map<String, String> body,
                                              @AuthenticationPrincipal User actor) {
        return shipmentRepository.findById(id).map(shipment -> {
            shipment.setDeliveryConfirmedByOperator(actor != null ? actor.getUsername() : "LOGISTICS_OPERATOR");
            shipment.setDeliveryConfirmedAt(LocalDateTime.now());
            shipmentRepository.save(shipment);

            auditService.logDetailedEvent(
                    actor != null ? actor.getUsername() : "LOGISTICS_OPERATOR",
                    actor != null ? actor.getRole().name() : "LOGISTICS_OPERATOR",
                    "DELIVERY_CONFIRMED_BY_OPERATOR",
                    "Shipment",
                    id.toString(),
                    shipment.getStatus(),
                    "DELIVERY_CONFIRMED",
                    body.getOrDefault("justificationReason", "Verified receiver OTP and proof of delivery"),
                    null,
                    "SUCCESS"
            );

            return ResponseEntity.ok(shipment);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/logistics-impact/corridor/{code}")
    @PreAuthorize("hasAuthority('SHIPMENT_VIEW') or hasAuthority('ANALYTICS_VIEW')")
    public ResponseEntity<LogisticsImpactDto> analyzeLogisticsImpact(@PathVariable String code) {
        LogisticsImpactDto impact = LogisticsImpactDto.builder()
                .corridorCode(code)
                .affectedVehiclesCount(4)
                .affectedShipmentsCount(3)
                .criticalCommodities(List.of("MEDICINE", "OXYGEN_CYLINDERS"))
                .estimatedMaxDelayMinutes(200) // 3h 20m
                .recommendation("Reroute convoy truck NER-07 via Umrangso low-risk bypass corridor.")
                .build();
        return ResponseEntity.ok(impact);
    }

    @Data
    public static class ProofOfDeliveryDto {
        private String receiverOtp;
        private String proofPhotoUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogisticsImpactDto {
        private String corridorCode;
        private int affectedVehiclesCount;
        private int affectedShipmentsCount;
        private List<String> criticalCommodities;
        private int estimatedMaxDelayMinutes;
        private String recommendation;
    }
}
