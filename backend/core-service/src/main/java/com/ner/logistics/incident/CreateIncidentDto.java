package com.ner.logistics.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIncidentDto {

    @NotBlank
    private String type; // LANDSLIDE, FLOOD, ROAD_BLOCKED, ROAD_DAMAGE, etc.

    @NotBlank
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    private String description;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private List<String> photoUrls;
}
