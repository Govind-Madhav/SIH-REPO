package com.ner.logistics.auth;

import com.ner.logistics.accessibility.geofence.EmergencyCorridorController;
import com.ner.logistics.audit.AuditService;
import com.ner.logistics.emergency.EmergencyResourceController;
import com.ner.logistics.sos.*;
import com.ner.logistics.user.User;
import com.ner.logistics.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmergencyOperatorGovernanceTest {

    @Mock private SosEventRepository sosEventRepository;
    @Mock private SosAckRepository sosAckRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks private SosService sosService;

    @Mock private AuditService auditService;
    @InjectMocks private EmergencyCorridorController emergencyCorridorController;

    @InjectMocks private EmergencyResourceController emergencyResourceController;

    @Test
    void testSosDuplicateDetectionWithinWindow() {
        SosEvent existingSos = SosEvent.builder()
                .id(99L)
                .triggeredBy("DRIVER1")
                .vehicleCode("NER-07")
                .status("TRIGGERED")
                .createdAt(LocalDateTime.now().minusMinutes(2)) // Created 2 mins ago
                .build();

        when(sosEventRepository.findByVehicleCodeAndStatusNot("NER-07", "RESOLVED")).thenReturn(List.of(existingSos));
        when(sosEventRepository.save(any(SosEvent.class))).thenReturn(existingSos);

        SosRequestDto dto = SosRequestDto.builder()
                .vehicleCode("NER-07")
                .latitude(25.1234)
                .longitude(92.5678)
                .message("Repeated panic button press")
                .build();

        SosEvent result = sosService.triggerSos(dto, "DRIVER1");
        assertEquals(99L, result.getId());
        assertTrue(result.getMessage().contains("UPDATED:"));
    }

    @Test
    void testSosAcknowledgementPersistenceByOperator() {
        SosEvent event = SosEvent.builder().id(101L).status("TRIGGERED").vehicleCode("NER-07").build();
        when(sosEventRepository.findById(101L)).thenReturn(Optional.of(event));
        when(sosEventRepository.save(any(SosEvent.class))).thenReturn(event);

        SosEvent acknowledged = sosService.acknowledgeSos(101L, "EMERGENCY_OPERATOR_01");

        assertEquals("ACKNOWLEDGED", acknowledged.getStatus());
        assertEquals("EMERGENCY_OPERATOR_01", acknowledged.getAcknowledgedBy());
        assertNotNull(acknowledged.getAcknowledgedAt());
    }

    @Test
    void testSosFalseAlarmWorkflowWithReason() {
        SosEvent event = SosEvent.builder().id(102L).status("ACKNOWLEDGED").vehicleCode("NER-07").build();
        when(sosEventRepository.findById(102L)).thenReturn(Optional.of(event));
        when(sosEventRepository.save(any(SosEvent.class))).thenReturn(event);

        SosEvent falseAlarm = sosService.markFalseAlarm(102L, "Driver pressed panic button accidentally during cab cleanup");

        assertEquals("FALSE_ALARM", falseAlarm.getStatus());
        assertTrue(falseAlarm.getResolutionNotes().contains("Driver pressed panic button accidentally"));
    }

    @Test
    void testEmergencyResourceDoubleAssignmentConflictPrevention() {
        User operator = User.builder().username("emergency_op").role(UserRole.EMERGENCY_OPERATOR).build();

        // Assign RES-TEAM-HAFLONG-01 to SOS #201
        ResponseEntity<?> resp1 = emergencyResourceController.assignResourceToSos("RES-TEAM-HAFLONG-01", "201", operator);
        assertEquals(HttpStatus.OK, resp1.getStatusCode());

        // Attempting to assign the SAME resource to SOS #202 while assigned to #201 should trigger 409 Conflict
        ResponseEntity<?> resp2 = emergencyResourceController.assignResourceToSos("RES-TEAM-HAFLONG-01", "202", operator);
        assertEquals(HttpStatus.CONFLICT, resp2.getStatusCode());
    }

    @Test
    void testEmergencyCorridorDeclarationWithExpiry() {
        User operator = User.builder().username("emergency_op").role(UserRole.EMERGENCY_OPERATOR).build();

        EmergencyCorridorController.EmergencyCorridorRestrictionDto dto = EmergencyCorridorController.EmergencyCorridorRestrictionDto.builder()
                .corridorCode("COR-NH27")
                .corridorName("NH-27 Haflong Mountain Pass")
                .restrictionType("EMERGENCY_ONLY")
                .justificationReason("Priority clearance for NDRF rescue convoy and ambulances")
                .build();

        ResponseEntity<?> response = emergencyCorridorController.declareEmergencyCorridor(dto, operator);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(auditService).logDetailedEvent(
                eq("emergency_op"), eq("EMERGENCY_OPERATOR"), eq("EMERGENCY_CORRIDOR_RESTRICTED"),
                eq("EmergencyCorridor"), eq("COR-NH27"), eq("ACCESSIBLE"), eq("EMERGENCY_ONLY"),
                eq("Priority clearance for NDRF rescue convoy and ambulances"), any(), eq("SUCCESS")
        );
    }
}
