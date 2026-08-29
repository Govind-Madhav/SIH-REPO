package com.ner.logistics.sos;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Table(name = "sos_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SosEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String triggeredBy; // Driver / Officer username

    private String vehicleCode;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    private Double latitude;

    private Double longitude;

    private String emergencyType; // LANDSLIDE_TRAPPED, VEHICLE_BREAKDOWN, MEDICAL_EMERGENCY

    private String message;

    private String status; // ACTIVE, DISPATCHED, RESOLVED

    private LocalDateTime createdAt;
}
