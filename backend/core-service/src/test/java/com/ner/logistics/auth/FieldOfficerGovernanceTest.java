package com.ner.logistics.auth;

import com.ner.logistics.accessibility.geofence.EmergencyCorridorController;
import com.ner.logistics.audit.AuditService;
import com.ner.logistics.fieldtask.FieldTaskController;
import com.ner.logistics.file.FileUploadController;
import com.ner.logistics.incident.*;
import com.ner.logistics.shipment.ShipmentRepository;
import com.ner.logistics.tracking.VehicleLocationRepository;
import com.ner.logistics.user.User;
import com.ner.logistics.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FieldOfficerGovernanceTest {

    @Mock private IncidentRepository incidentRepository;
    @Mock private VehicleLocationRepository vehicleLocationRepository;
    @Mock private ShipmentRepository shipmentRepository;
    @Mock private SeverityEngineService severityEngineService;
    @Mock private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
    @Mock private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @InjectMocks private IncidentService incidentService;

    @Mock private AuditService auditService;
    @InjectMocks private FieldTaskController fieldTaskController;

    @InjectMocks private FileUploadController fileUploadController;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    void testGeospatialNerBoundaryValidationFailureOutsideNER() {
        CreateIncidentDto dto = CreateIncidentDto.builder()
                .type("LANDSLIDE")
                .reportedSeverity("HIGH")
                .latitude(10.5000) // Outside NER (Kerala)
                .longitude(76.2000)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            incidentService.createIncident(dto, "field_officer_01");
        });
    }

    @Test
    void testIncidentCreationWithinNerBoundary() {
        CreateIncidentDto dto = CreateIncidentDto.builder()
                .type("LANDSLIDE")
                .reportedSeverity("HIGH")
                .latitude(25.1833) // Dima Hasao, Assam (Inside NER)
                .longitude(92.8333)
                .build();

        Point p = geometryFactory.createPoint(new Coordinate(92.8333, 25.1833));
        p.setSRID(4326);

        var severityResult = new SeverityEngineService.SeverityRecommendationResult("HIGH", 80, 85.0);
        when(severityEngineService.calculateSeverityAndConfidence(any())).thenReturn(severityResult);
        when(vehicleLocationRepository.findNearbyVehicles(anyDouble(), anyDouble(), anyDouble())).thenReturn(Collections.emptyList());

        Incident incident = Incident.builder()
                .id(201L)
                .type("LANDSLIDE")
                .latitude(25.1833)
                .longitude(92.8333)
                .verificationStatus("FIELD_CONFIRMED")
                .location(p)
                .build();

        when(incidentRepository.save(any(Incident.class))).thenReturn(incident);
        when(incidentRepository.findById(201L)).thenReturn(java.util.Optional.of(incident));

        Incident created = incidentService.createIncident(dto, "field_officer_01");

        assertNotNull(created);
        assertEquals("FIELD_CONFIRMED", created.getVerificationStatus());
    }

    @Test
    void testPhotoUploadRejectionForExecutableExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "malicious_script.exe", "application/x-msdownload", "binary_content".getBytes());
        ResponseEntity<?> response = fileUploadController.uploadFile(file, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid file type"));
    }

    @Test
    void testFieldTaskAcknowledgementWorkflow() {
        User officer = User.builder().username("field_officer_01").role(UserRole.FIELD_OFFICER).build();

        Map<String, String> body = Map.of("status", "ACKNOWLEDGED", "notes", "On my way to NH-27 landslide sector");
        ResponseEntity<?> response = fieldTaskController.updateTaskStatus("TASK-HAFLONG-104", body, officer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditService).logDetailedEvent(
                eq("field_officer_01"), eq("FIELD_OFFICER"), eq("FIELD_TASK_STATUS_UPDATED"),
                eq("FieldTask"), eq("TASK-HAFLONG-104"), eq("RECEIVED"), eq("ACKNOWLEDGED"),
                eq("On my way to NH-27 landslide sector"), any(), eq("SUCCESS")
        );
    }
}
