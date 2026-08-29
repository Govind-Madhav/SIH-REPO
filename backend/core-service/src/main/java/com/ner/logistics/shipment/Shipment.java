package com.ner.logistics.shipment;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleCode; // Associated vehicle (e.g. NER-07)

    @Column(nullable = false)
    private String commodityType; // MEDICINE, OXYGEN, FOOD, FUEL, RELIEF_GOODS

    @Column(nullable = false)
    private String priority; // NORMAL, HIGH, CRITICAL

    private String origin;

    private String destination;

    private String status; // IN_TRANSIT, DELIVERED, DELAYED, AT_RISK
}
