package com.ner.logistics.shipment;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    private String assignedDriverUsername;

    @Column(nullable = false)
    private String commodityType; // MEDICINE, OXYGEN_CYLINDERS, BABY_FOOD, FUEL, FOOD, AGRICULTURAL_PRODUCE, CONSTRUCTION_MATERIAL

    @Column(nullable = false)
    private String priority; // NORMAL, HIGH, CRITICAL

    private String origin;

    private String destination;

    private String status; // PLANNED, ASSIGNED, IN_TRANSIT, DELAYED, DELIVERED, CANCELLED, ON_HOLD

    private String receiverOtp;

    private String proofPhotoUrl;

    private String deliveryConfirmedByOperator;

    private LocalDateTime deliveryConfirmedAt;
}
