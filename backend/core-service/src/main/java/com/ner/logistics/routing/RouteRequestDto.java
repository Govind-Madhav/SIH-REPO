package com.ner.logistics.routing;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDto {

    private String vehicleCode;

    @NotNull
    private Double originLat;

    @NotNull
    private Double originLng;

    @NotNull
    private Double destLat;

    @NotNull
    private Double destLng;

    private Boolean avoidHazardZones;
}
