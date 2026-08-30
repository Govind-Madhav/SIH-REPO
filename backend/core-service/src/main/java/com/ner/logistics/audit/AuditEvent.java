package com.ner.logistics.audit;

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
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actor;

    private String actorRole;

    @Column(nullable = false)
    private String action; // INCIDENT_STATUS_CHANGED, SOS_ACKNOWLEDGED, VEHICLE_STATUS_CHANGED, ROUTE_REROUTED, DEVICE_REGISTERED, DEVICE_REVOKED, USER_SUSPENDED, SHIPMENT_STATUS_OVERRIDDEN

    private String resourceType;

    private String resourceId;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Column(columnDefinition = "TEXT")
    private String justificationReason;

    private String clientIp;

    private String result; // SUCCESS, DENIED, FAILED

    private LocalDateTime timestamp;
}
