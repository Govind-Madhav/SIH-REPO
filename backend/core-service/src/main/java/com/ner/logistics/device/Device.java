package com.ner.logistics.device;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String imei;

    private String vehicleCode;

    private String deviceName;

    private String deviceType; // AIS140_GPS, IOT_SENSOR, MOBILE_GATEWAY

    private String apiKeyHash; // SHA-256 hashed API key

    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE, REVOKED

    private LocalDateTime createdAt;

    private LocalDateTime lastSeenAt;
}
