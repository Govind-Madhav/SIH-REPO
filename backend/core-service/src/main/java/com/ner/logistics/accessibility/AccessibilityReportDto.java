package com.ner.logistics.accessibility;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessibilityReportDto {

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Status is required")
    private String status; // OPEN, PARTIALLY_ACCESSIBLE, RESTRICTED, BLOCKED

    private String condition; // LANDSLIDE, FLOOD, ROAD_DAMAGE, BRIDGE_DAMAGE, TRAFFIC_CONGESTION, OTHER

    private String description;

    private String corridorCode;

    private String clientEventId;
}
