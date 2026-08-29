package com.ner.logistics.incident;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // LANDSLIDE, FLOOD, ROAD_BLOCKED, ROAD_DAMAGE, BRIDGE_DAMAGE

    @Column(nullable = false)
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    private String description;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    private Double latitude;

    private Double longitude;

    private String reportedBy;

    private String status; // ACTIVE, RESOLVED, UNDER_INVESTIGATION

    private LocalDateTime createdAt;
}
