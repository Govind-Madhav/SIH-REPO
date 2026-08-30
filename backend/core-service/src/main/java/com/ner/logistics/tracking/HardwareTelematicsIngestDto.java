package com.ner.logistics.tracking;

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
public class HardwareTelematicsIngestDto {

    @NotNull
    private String imei; // Hardware GPS IMEI / Device ID

    private String vehicleCode; // Mapped truck code e.g. NER-07

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Double speedKmh;

    private Double headingDegrees;

    private Double altitudeMeters;

    private Boolean ignitionOn;

    private Boolean sosButtonPressed; // AIS-140 Emergency panic button hardware wire

    private String protocolStandard; // AIS140, MQTT, TELTONIKA, CONCOX, TATA_FLEETEDGE

    private LocalDateTime timestamp;
}
