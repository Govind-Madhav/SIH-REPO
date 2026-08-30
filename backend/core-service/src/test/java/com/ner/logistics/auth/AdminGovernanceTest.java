package com.ner.logistics.auth;

import com.ner.logistics.accessibility.geofence.GeofenceCorridorController;
import com.ner.logistics.audit.AuditService;
import com.ner.logistics.common.health.SystemHealthController;
import com.ner.logistics.device.Device;
import com.ner.logistics.device.DeviceController;
import com.ner.logistics.device.DeviceService;
import com.ner.logistics.emergency.EmergencyResourceController;
import com.ner.logistics.governance.DataGovernanceController;
import com.ner.logistics.notification.AlertPolicyController;
import com.ner.logistics.risk.controller.MlModelGovernanceController;
import com.ner.logistics.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminGovernanceTest {

    @Mock private UserRepository userRepository;
    @Mock private AuditService auditService;
    @InjectMocks private UserController userController;

    @InjectMocks private MlModelGovernanceController mlModelGovernanceController;
    @InjectMocks private AlertPolicyController alertPolicyController;
    @InjectMocks private SystemHealthController systemHealthController;

    @Mock private DeviceService deviceService;
    @InjectMocks private DeviceController deviceController;

    @InjectMocks private GeofenceCorridorController geofenceCorridorController;
    @InjectMocks private EmergencyResourceController emergencyResourceController;
    @InjectMocks private DataGovernanceController dataGovernanceController;

    @Test
    void testUserSuspensionWithMandatoryJustification() {
        User user = User.builder().id(10L).username("operator1").role(UserRole.LOGISTICS_OPERATOR).status(UserAccountStatus.ACTIVE).build();
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        UserController.UserStatusChangeDto dto = new UserController.UserStatusChangeDto();
        dto.setJustificationReason("Security compliance audit requirement");

        ResponseEntity<?> response = userController.suspendUser(10L, dto, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(UserAccountStatus.SUSPENDED, user.getStatus());

        verify(auditService).logDetailedEvent(
                eq("ADMIN"), eq("ADMIN"), eq("USER_SUSPENDED"), eq("User"), eq("10"),
                eq("ACTIVE"), eq("SUSPENDED"), eq("Security compliance audit requirement"), any(), eq("SUCCESS")
        );
    }

    @Test
    void testUserSuspensionRejectedWithoutJustification() {
        UserController.UserStatusChangeDto dto = new UserController.UserStatusChangeDto();
        dto.setJustificationReason(""); // Empty

        ResponseEntity<?> response = userController.suspendUser(10L, dto, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testMlModelGovernanceAndRollback() {
        ResponseEntity<List<MlModelGovernanceController.MlModelMetadataDto>> modelsResp = mlModelGovernanceController.getModelMetadata();
        assertEquals(HttpStatus.OK, modelsResp.getStatusCode());
        assertEquals(2, modelsResp.getBody().size());

        MlModelGovernanceController.ModelActionDto rollbackDto = new MlModelGovernanceController.ModelActionDto();
        rollbackDto.setModelId("xgb-landslide-ner-v2.1");
        rollbackDto.setVersionToDeploy("2.0.0");
        rollbackDto.setJustificationReason("Performance regression detected in heavy rainfall");

        ResponseEntity<?> rollbackResp = mlModelGovernanceController.rollbackModel(rollbackDto, null);
        assertEquals(HttpStatus.OK, rollbackResp.getStatusCode());

        verify(auditService).logDetailedEvent(
                eq("ADMIN"), eq("ADMIN"), eq("ML_MODEL_ROLLED_BACK"), eq("MLModel"), eq("xgb-landslide-ner-v2.1"),
                eq("v2.1.0"), eq("2.0.0"), eq("Performance regression detected in heavy rainfall"), any(), eq("SUCCESS")
        );
    }

    @Test
    void testSystemHealthAndCredentialRotationWithoutSecretLeak() {
        ResponseEntity<Map<String, Object>> healthResp = systemHealthController.getSystemHealth();
        assertEquals(HttpStatus.OK, healthResp.getStatusCode());
        assertEquals("HEALTHY", healthResp.getBody().get("overallStatus"));

        ResponseEntity<?> rotateResp = systemHealthController.rotateApiCredential("INT-IMD-WEATHER", Map.of("justificationReason", "Annual key rotation"), null);
        assertEquals(HttpStatus.OK, rotateResp.getStatusCode());

        verify(auditService).logDetailedEvent(
                eq("ADMIN"), eq("ADMIN"), eq("API_CREDENTIAL_ROTATED"), eq("IntegrationCredential"), eq("INT-IMD-WEATHER"),
                any(), any(), eq("Annual key rotation"), any(), eq("SUCCESS")
        );
    }

    @Test
    void testDeviceAssignmentAndUnassignment() {
        Device mockDevice = Device.builder().imei("86753090011").vehicleCode("NER-07").status("ACTIVE").build();
        when(deviceService.assignDeviceToVehicle("86753090011", "NER-07")).thenReturn(mockDevice);

        ResponseEntity<Device> assignResp = deviceController.assignDeviceToVehicle("86753090011", "NER-07", null);
        assertEquals(HttpStatus.OK, assignResp.getStatusCode());
        assertEquals("NER-07", assignResp.getBody().getVehicleCode());

        verify(auditService).logDetailedEvent(
                eq("ADMIN"), eq("ADMIN"), eq("DEVICE_ASSIGNED"), eq("Device"), eq("86753090011"),
                any(), eq("NER-07"), any(), any(), eq("SUCCESS")
        );
    }

    @Test
    void testSensitiveDataExportWithAuditing() {
        DataGovernanceController.SensitiveExportRequestDto exportDto = new DataGovernanceController.SensitiveExportRequestDto();
        exportDto.setDatasetType("DRIVER_GPS_HISTORY");
        exportDto.setRecordCount(1500);
        exportDto.setJustificationReason("Official accident investigation inquiry");

        ResponseEntity<?> exportResp = dataGovernanceController.exportSensitiveData(exportDto, null);
        assertEquals(HttpStatus.OK, exportResp.getStatusCode());

        verify(auditService).logDetailedEvent(
                eq("ADMIN"), eq("ADMIN"), eq("SENSITIVE_DATA_EXPORTED"), eq("Dataset"), eq("DRIVER_GPS_HISTORY"),
                any(), eq("Exported Records Count: 1500"), eq("Official accident investigation inquiry"), any(), eq("SUCCESS")
        );
    }
}
