package com.ner.logistics.tracking;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleCode;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    private Double latitude;

    private Double longitude;

    private Double speedKmh;

    private Double headingDegrees;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
