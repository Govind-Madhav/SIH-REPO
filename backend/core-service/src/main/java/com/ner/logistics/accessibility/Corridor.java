package com.ner.logistics.accessibility;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "corridors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Corridor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // e.g. NH-27 Guwahati -> Silchar

    private String code; // e.g. COR-NH27

    private Double accessibilityScorePct; // 0 to 100%

    private String status; // ACCESSIBLE, DEGRADED, HIGH_RISK, BLOCKED

    private String startPoint;

    private String endPoint;

    private Double lengthKm;
}
