package com.ner.logistics.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSafetyBubbleDto {

    private String vehicleCode;

    private String safetyZone; // SAFE_ZONE, WARNING_ZONE, DANGER_ZONE

    private Double distanceToHazardKm;

    private String hazardType; // LANDSLIDE, FLOOD, ROAD_BLOCKAGE

    private String recommendedDriverAction;
}
