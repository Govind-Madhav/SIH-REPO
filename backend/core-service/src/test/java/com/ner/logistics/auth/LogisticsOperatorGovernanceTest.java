package com.ner.logistics.auth;

import com.ner.logistics.audit.AuditService;
import com.ner.logistics.decision.LogisticsDecisionController;
import com.ner.logistics.decision.LogisticsDecisionService;
import com.ner.logistics.shipment.Shipment;
import com.ner.logistics.shipment.ShipmentController;
import com.ner.logistics.shipment.ShipmentRepository;
import com.ner.logistics.shipment.SupplyGapAnalysisService;
import com.ner.logistics.user.User;
import com.ner.logistics.user.UserRole;
import com.ner.logistics.vehicle.Vehicle;
import com.ner.logistics.vehicle.VehicleController;
import com.ner.logistics.vehicle.VehicleRepository;
import com.ner.logistics.vehicle.VehicleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogisticsOperatorGovernanceTest {

    @Mock private VehicleRepository vehicleRepository;
    @InjectMocks private VehicleService vehicleService;
    @Mock private VehicleService mockVehicleService;
    @Mock private AuditService auditService;

    @InjectMocks private VehicleController vehicleController;

    @Mock private ShipmentRepository shipmentRepository;
    @Mock private SupplyGapAnalysisService supplyGapAnalysisService;
    @InjectMocks private ShipmentController shipmentController;

    @Mock private LogisticsDecisionService logisticsDecisionService;
    @InjectMocks private LogisticsDecisionController logisticsDecisionController;

    @Test
    void testDriverAssignmentConsistencyValidation() {
        Vehicle existingVehicle = Vehicle.builder().code("NER-01").assignedDriverUsername("driver1").status("ASSIGNED").build();
        when(vehicleRepository.findByAssignedDriverUsername("driver1")).thenReturn(Optional.of(existingVehicle));

        // Attempting to assign driver1 to NER-07 while already assigned to NER-01
        assertThrows(IllegalArgumentException.class, () -> {
            vehicleService.assignDriverToVehicle("NER-07", "driver1");
        });
    }

    @Test
    void testInvalidShipmentStateTransitionRejection() {
        Shipment deliveredShipment = Shipment.builder().id(101L).status("DELIVERED").vehicleCode("NER-07").commodityType("MEDICINE").priority("CRITICAL").build();
        when(shipmentRepository.findById(101L)).thenReturn(Optional.of(deliveredShipment));

        ResponseEntity<?> response = shipmentController.updateStatus(101L, Map.of("status", "IN_TRANSIT"), null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeliveryConfirmationByLogisticsOperator() {
        Shipment shipment = Shipment.builder().id(102L).status("DELIVERED").vehicleCode("NER-07").commodityType("OXYGEN_CYLINDERS").priority("CRITICAL").build();
        when(shipmentRepository.findById(102L)).thenReturn(Optional.of(shipment));

        User operator = User.builder().username("logistics_operator_01").role(UserRole.LOGISTICS_OPERATOR).build();
        ResponseEntity<?> response = shipmentController.confirmDelivery(102L, Map.of("justificationReason", "Verified receiver OTP and photo proof"), operator);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("logistics_operator_01", shipment.getDeliveryConfirmedByOperator());

        verify(auditService).logDetailedEvent(
                eq("logistics_operator_01"), eq("LOGISTICS_OPERATOR"), eq("DELIVERY_CONFIRMED_BY_OPERATOR"),
                eq("Shipment"), eq("102"), eq("DELIVERED"), eq("DELIVERY_CONFIRMED"), any(), any(), eq("SUCCESS")
        );
    }

    @Test
    void testLogisticsOperatorCannotApproveEmergencyEscalation() {
        LogisticsDecisionController.DecisionApprovalDto dto = new LogisticsDecisionController.DecisionApprovalDto();
        dto.setDecisionType("ESCALATE_EMERGENCY");
        dto.setTargetEntity("State Disaster Command");

        User operator = User.builder().username("logistics_operator_01").role(UserRole.LOGISTICS_OPERATOR).build();
        ResponseEntity<?> response = logisticsDecisionController.approveDecision(dto, operator);

        // Access Denied: ESCALATE_EMERGENCY decisions belong exclusively to EMERGENCY_OPERATOR role.
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testLogisticsOperatorCanApproveRerouteDecision() {
        LogisticsDecisionController.DecisionApprovalDto dto = new LogisticsDecisionController.DecisionApprovalDto();
        dto.setDecisionType("REROUTE_VEHICLE");
        dto.setTargetEntity("Convoy Truck NER-07");
        dto.setJustificationReason("Approved Haflong bypass corridor rerouting due to landslide");

        User operator = User.builder().username("logistics_operator_01").role(UserRole.LOGISTICS_OPERATOR).build();
        ResponseEntity<?> response = logisticsDecisionController.approveDecision(dto, operator);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditService).logDetailedEvent(
                eq("logistics_operator_01"), eq("LOGISTICS_OPERATOR"), eq("DECISION_APPROVED"),
                eq("DecisionRecommendation"), any(), any(), eq("APPROVED"), eq("Approved Haflong bypass corridor rerouting due to landslide"), any(), eq("SUCCESS")
        );
    }
}
