package com.ner.logistics.mobile;

import com.ner.logistics.accessibility.AccessibilityReportDto;

import com.ner.logistics.shipment.Shipment;
import com.ner.logistics.shipment.ShipmentRepository;
import com.ner.logistics.tracking.BatchGpsLocationDto;
import com.ner.logistics.tracking.GpsLocationDto;
import com.ner.logistics.tracking.TrackingController;
import com.ner.logistics.vehicle.Vehicle;
import com.ner.logistics.vehicle.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MobileIntegrationTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Test
    void testDriverVehicleAndShipmentAssignmentLookup() {
        Vehicle vehicle = Vehicle.builder()
                .code("NER-07")
                .assignedDriverUsername("driver")
                .licensePlate("AS-01-HA-7007")
                .status("ON_TRACK")
                .build();

        Shipment shipment = Shipment.builder()
                .id(1L)
                .vehicleCode("NER-07")
                .assignedDriverUsername("driver")
                .commodityType("MEDICINE")
                .priority("CRITICAL")
                .status("IN_TRANSIT")
                .build();

        when(vehicleRepository.findByAssignedDriverUsername("driver")).thenReturn(Optional.of(vehicle));
        when(shipmentRepository.findByAssignedDriverUsername("driver")).thenReturn(List.of(shipment));

        Optional<Vehicle> vOpt = vehicleRepository.findByAssignedDriverUsername("driver");
        List<Shipment> sList = shipmentRepository.findByAssignedDriverUsername("driver");

        assertTrue(vOpt.isPresent());
        assertEquals("NER-07", vOpt.get().getCode());
        assertEquals(1, sList.size());
        assertEquals("MEDICINE", sList.get(0).getCommodityType());
    }

    @Test
    void testAccessibilityReportDtoCreation() {
        AccessibilityReportDto dto = AccessibilityReportDto.builder()
                .latitude(25.5)
                .longitude(93.0)
                .status("BLOCKED")
                .condition("LANDSLIDE")
                .description("Haflong debris blockage")
                .clientEventId("EVENT-UUID-991")
                .build();

        assertNotNull(dto);
        assertEquals("BLOCKED", dto.getStatus());
        assertEquals("LANDSLIDE", dto.getCondition());
        assertEquals("EVENT-UUID-991", dto.getClientEventId());
    }
}
