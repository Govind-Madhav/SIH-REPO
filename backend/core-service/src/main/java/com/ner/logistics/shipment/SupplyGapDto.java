package com.ner.logistics.shipment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplyGapDto {

    private String district;

    private String commodityType; // OXYGEN_CYLINDERS, ESSENTIAL_MEDICINE, DIESEL_FUEL, RICE_STAPLE

    private String riskLevel; // CRITICAL, HIGH, MEDIUM, LOW

    private Integer estimatedDelayHours;

    private Integer affectedShipmentsCount;

    private String recommendedAction; // PRIORITIZE_ALTERNATIVE_SUPPLY, AIRLIFT_EMERGENCY_STOCK, REROUTE_CONVOY

    private String rationale;
}
