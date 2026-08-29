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
    private String type; // LANDSLIDE, FLOOD, ROAD_BLOCKED, ROAD_DAMAGE, BRIDGE_DAMAGE, HEAVY_RAIN, TRAFFIC_CONGESTION

    @Column(nullable = false)
    private String reportedSeverity; // LOW, MEDIUM, HIGH, CRITICAL (field officer report)

    private String recommendedSeverity; // LOW, MEDIUM, HIGH, CRITICAL (system calculated)

    private Integer severityScore; // 0 to 100

    private String description;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    private Double latitude;

    private Double longitude;

    private String districtName;

    private String reportedBy;

    @Column(nullable = false)
    private String verificationStatus; // REPORTED, UNDER_VERIFICATION, VERIFIED, ACTIVE, RESOLVED

    private Double confidenceLevel; // 0% to 100% based on geographic clustering

    @Column(columnDefinition = "TEXT")
    private String photoUrlsJson; // Evidence photo URLs

    private String status; // ACTIVE, RESOLVED, UNDER_INVESTIGATION

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.verificationStatus == null) {
            this.verificationStatus = "REPORTED";
        }
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
