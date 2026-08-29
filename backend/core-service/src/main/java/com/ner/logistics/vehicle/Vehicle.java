package com.ner.logistics.vehicle;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // e.g. NER-07

    private String licensePlate;

    private String vehicleType; // e.g. Emergency Truck, Fuel Tanker

    private Double capacityTons;

    private Long driverId;

    @Column(nullable = false)
    private String status; // ON_TRACK, DELAYED, AT_RISK
}
