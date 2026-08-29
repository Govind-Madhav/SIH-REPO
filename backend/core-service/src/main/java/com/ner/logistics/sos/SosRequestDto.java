package com.ner.logistics.sos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SosRequestDto {

    private String vehicleCode;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String emergencyType;

    private String message;
}
