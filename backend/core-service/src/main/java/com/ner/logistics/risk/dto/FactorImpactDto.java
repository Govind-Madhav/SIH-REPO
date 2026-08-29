package com.ner.logistics.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactorImpactDto {
    private String factor;
    private String impact; // LOW, MEDIUM, HIGH, CRITICAL
}
