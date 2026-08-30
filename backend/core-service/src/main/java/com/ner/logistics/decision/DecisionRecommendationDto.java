package com.ner.logistics.decision;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionRecommendationDto {

    private String decisionType; // REROUTE_VEHICLE, HOLD_SHIPMENT, DISPATCH_FIELD_OFFICER, ESCALATE_EMERGENCY, PRIORITIZE_SHIPMENT, MONITOR

    private String priority; // CRITICAL, HIGH, MEDIUM, NORMAL

    private String targetEntity; // e.g. Vehicle NER-07 (Medical Convoy)

    private String affectedVehicle;

    private String affectedShipment;

    private String recommendedAction; // Actionable instruction string

    private String destinationDistrict; // Dima Hasao, Silchar, etc.

    private String rationale;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
