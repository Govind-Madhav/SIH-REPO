package com.ner.logistics.auth;

import com.ner.logistics.audit.AuditService;
import com.ner.logistics.mobile.DriverAssignmentDto;
import com.ner.logistics.mobile.MobileDriverController;
import com.ner.logistics.routing.GraphHopperRoutingService;
import com.ner.logistics.shipment.Shipment;
import com.ner.logistics.shipment.ShipmentRepository;
import com.ner.logistics.sos.SosEventRepository;
import com.ner.logistics.user.User;
import com.ner.logistics.user.UserRole;
import com.ner.logistics.vehicle.Vehicle;
import com.ner.logistics.vehicle.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DriverGovernanceTest {

    @Mock private VehicleRepository vehicleRepository;
    @Mock private ShipmentRepository shipmentRepository;
    @Mock private SosEventRepository sosEventRepository;
    @Mock private GraphHopperRoutingService graphHopperRoutingService;
    @Mock private AuditService auditService;

    @InjectMocks private MobileDriverController mobileDriverController;

    @Test
    void testDriverAccessToAssignedVehicleAndShipments() {
        User driver = User.builder().username("driver_ner07").role(UserRole.DRIVER).fullName("Rahul Sharma").build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(driver);
        when(auth.getName()).thenReturn("driver_ner07");

        Vehicle vehicle = Vehicle.builder().code("NER-07").assignedDriverUsername("driver_ner07").status("ON_TRACK").build();
        Shipment shipment = Shipment.builder().id(501L).vehicleCode("NER-07").assignedDriverUsername("driver_ner07").status("IN_TRANSIT").build();

        when(vehicleRepository.findByAssignedDriverUsername("driver_ner07")).thenReturn(Optional.of(vehicle));
        when(shipmentRepository.findByAssignedDriverUsername("driver_ner07")).thenReturn(List.of(shipment));

        ResponseEntity<DriverAssignmentDto> response = mobileDriverController.getDriverContext(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        DriverAssignmentDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("NER-07", dto.getAssignedVehicle().getCode());
        assertEquals(1, dto.getAssignedShipments().size());
    }

    @Test
    void testStrictRouteOwnershipFailureWhenUnassigned() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("unassigned_driver");
        when(vehicleRepository.findByAssignedDriverUsername("unassigned_driver")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            mobileDriverController.getAssignedRoute(auth);
        });
    }

    @Test
    void testDriverRejectionWhenUpdatingOtherDriversShipment() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("driver_01");

        Shipment otherDriverShipment = Shipment.builder()
                .id(999L)
                .assignedDriverUsername("driver_02") // Assigned to another driver
                .status("IN_TRANSIT")
                .build();

        when(shipmentRepository.findById(999L)).thenReturn(Optional.of(otherDriverShipment));

        assertThrows(AccessDeniedException.class, () -> {
            mobileDriverController.updateShipmentStatus(999L, "ARRIVED_DESTINATION", auth);
        });
    }

    @Test
    void testDriverRejectionWhenAttemptingOfficialDeliveryConfirmation() {
        User driver = User.builder().username("driver_ner07").role(UserRole.DRIVER).build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(driver);
        when(auth.getName()).thenReturn("driver_ner07");

        Shipment driverShipment = Shipment.builder()
                .id(501L)
                .assignedDriverUsername("driver_ner07")
                .status("ARRIVED_DESTINATION")
                .build();

        when(shipmentRepository.findById(501L)).thenReturn(Optional.of(driverShipment));

        // Driver attempting official DELIVERY_CONFIRMED without DELIVERY_CONFIRM permission throws AccessDeniedException
        assertThrows(AccessDeniedException.class, () -> {
            mobileDriverController.updateShipmentStatus(501L, "DELIVERY_CONFIRMED", auth);
        });
    }

    @Test
    void testDriverSuccessfulTransitStatusUpdate() {
        User driver = User.builder().username("driver_ner07").role(UserRole.DRIVER).build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(driver);
        when(auth.getName()).thenReturn("driver_ner07");

        Shipment driverShipment = Shipment.builder()
                .id(501L)
                .assignedDriverUsername("driver_ner07")
                .status("IN_TRANSIT")
                .build();

        when(shipmentRepository.findById(501L)).thenReturn(Optional.of(driverShipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(driverShipment);

        ResponseEntity<Shipment> response = mobileDriverController.updateShipmentStatus(501L, "DELAYED_LANDSLIDE", auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditService).logDetailedEvent(
                eq("driver_ner07"), eq("DRIVER"), eq("DRIVER_SHIPMENT_STATUS_UPDATED"),
                eq("Shipment"), eq("501"), eq("IN_TRANSIT"), eq("DELAYED_LANDSLIDE"),
                any(), any(), eq("SUCCESS")
        );
    }

    @Test
    void testDriverEnRouteHazardFlagging() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("driver_ner07");

        MobileDriverController.DriverHazardFlagDto dto = MobileDriverController.DriverHazardFlagDto.builder()
                .hazardType("ROCKFALL")
                .latitude(25.1500)
                .longitude(92.7000)
                .description("Fresh rockfall debris blocking eastbound lane on NH-27")
                .build();

        ResponseEntity<?> response = mobileDriverController.flagEnRouteHazard(dto, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("REPORTED_HAZARD", body.get("status"));
        assertEquals("ROCKFALL", body.get("hazardType"));

        verify(auditService).logDetailedEvent(
                eq("driver_ner07"), eq("DRIVER"), eq("ROAD_HAZARD_FLAGGED"),
                eq("RoadHazard"), anyString(), eq("CLEAR"), eq("REPORTED_HAZARD"),
                anyString(), any(), eq("SUCCESS")
        );
    }
}
