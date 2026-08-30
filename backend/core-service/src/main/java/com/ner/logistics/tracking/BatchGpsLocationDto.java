package com.ner.logistics.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchGpsLocationDto {
    private String vehicleCode;
    private List<GpsLocationDto> events;
}
