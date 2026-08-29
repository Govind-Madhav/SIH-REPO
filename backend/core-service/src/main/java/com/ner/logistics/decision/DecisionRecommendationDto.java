package com.ner.logistics.decision;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionRecommendationDto {

    private String decisionType; // REROUTE_VEHICLE, HOLD_SHIPMENT, DISPATCH_OFFICER, MARK_CORRIDOR_INACCESSIBLE

    private String priority; // CRITICAL, HIGH, MEDIUM

    private String targetEntity; // Vehicle NER-07, Fuel Tanker NER-04, Corridor NH-27

    private String recommendedAction; // Actionable instruction string

    private String destinationDistrict; // Dima Hasao, Silchar, etc.

    private String rationale;
}
