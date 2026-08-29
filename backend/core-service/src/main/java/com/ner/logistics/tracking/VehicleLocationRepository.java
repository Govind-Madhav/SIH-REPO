package com.ner.logistics.tracking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, Long> {
    List<VehicleLocation> findByVehicleCodeOrderByTimestampDesc(String vehicleCode);

    // Spatial Query: Find locations within distance (meters) of a point
    @Query(value = "SELECT * FROM vehicle_locations vl WHERE ST_DWithin(vl.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326), :distanceMeters)", nativeQuery = true)
    List<VehicleLocation> findNearbyVehicles(@Param("lat") double lat, @Param("lng") double lng, @Param("distanceMeters") double distanceMeters);
}
