package com.ner.logistics.tracking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GpsLocationDto {

    @NotBlank
    private String vehicleCode;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Double speedKmh;

    private Double headingDegrees;

    private LocalDateTime timestamp;
}
