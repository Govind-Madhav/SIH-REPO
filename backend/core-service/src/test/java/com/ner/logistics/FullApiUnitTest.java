package com.ner.logistics;

import com.ner.logistics.accessibility.*;
import com.ner.logistics.auth.*;
import com.ner.logistics.file.FileUploadController;
import com.ner.logistics.file.FileUploadResponseDto;
import com.ner.logistics.i18n.I18nController;
import com.ner.logistics.i18n.I18nService;
import com.ner.logistics.i18n.LanguageDto;
import com.ner.logistics.incident.CreateIncidentDto;
import com.ner.logistics.incident.Incident;
import com.ner.logistics.incident.IncidentController;
import com.ner.logistics.incident.IncidentService;
import com.ner.logistics.mobile.DriverAssignmentDto;
import com.ner.logistics.mobile.MobileDriverController;
import com.ner.logistics.routing.GraphHopperRoutingService;
import com.ner.logistics.sos.*;
import com.ner.logistics.tracking.*;
import com.ner.logistics.user.UserRepository;
import com.ner.logistics.vehicle.Vehicle;
import com.ner.logistics.vehicle.VehicleRepository;
import com.ner.logistics.shipment.Shipment;
import com.ner.logistics.shipment.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FullApiUnitTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider tokenProvider;
    @Mock private OtpService otpService;

    @InjectMocks private AuthController authController;

    @Mock private VehicleRepository vehicleRepository;
    @Mock private ShipmentRepository shipmentRepository;
    @Mock private SosEventRepository sosEventRepository;
    @Mock private GraphHopperRoutingService graphHopperRoutingService;

    @InjectMocks private MobileDriverController mobileDriverController;

    @Mock private TrackingKafkaProducer trackingKafkaProducer;
    @Mock private RedisTrackingService redisTrackingService;
    @Mock private VehicleLocationRepository vehicleLocationRepository;
    @Mock private com.ner.logistics.incident.IncidentRepository incidentRepository;
    @Mock private com.ner.logistics.device.DeviceService deviceService;
    @Mock private SosService sosService;

    @InjectMocks private TrackingController trackingController;

    @Mock private IncidentService incidentService;
    @Mock private FileUploadController fileUploadController;

    @InjectMocks private IncidentController incidentController;

    @Mock private AccessibilityEngineService accessibilityEngineService;
    @Mock private DistrictAccessibilityService districtAccessibilityService;

    @InjectMocks private AccessibilityController accessibilityController;

    @Mock private I18nService i18nService;

    @InjectMocks private I18nController i18nController;

    @Test
    void testAuthEndpointsExecution() {
        // 1. Send OTP
        OtpSendRequestDto sendDto = new OtpSendRequestDto("+919876543213");
        when(otpService.sendOtp(sendDto)).thenReturn("OTP sent successfully to +919876543213. (Demo OTP: 123456)");

        ResponseEntity<Map<String, String>> sendResp = authController.sendOtp(sendDto);
        assertEquals(HttpStatus.OK, sendResp.getStatusCode());
        assertEquals("SUCCESS", sendResp.getBody().get("status"));

        // 2. Verify OTP
        OtpVerifyRequestDto verifyDto = new OtpVerifyRequestDto("+919876543213", "123456");
        AuthResponse mockAuth = AuthResponse.builder()
                .token("mock-jwt-token")
                .username("driver_919876543213")
                .role("DRIVER")
                .roles(List.of("DRIVER"))
                .permissions(List.of("SOS_TRIGGER", "VEHICLE_VIEW_SELF"))
                .build();

        when(otpService.verifyOtpAndLogin(verifyDto)).thenReturn(mockAuth);

        ResponseEntity<AuthResponse> verifyResp = authController.verifyOtp(verifyDto);
        assertEquals(HttpStatus.OK, verifyResp.getStatusCode());
        assertEquals("DRIVER", verifyResp.getBody().getRole());
    }

    @Test
    void testMobileDriverEndpointsExecution() {
        Vehicle vehicle = Vehicle.builder().code("NER-07").licensePlate("AS-01-HA-7007").build();
        Shipment shipment = Shipment.builder().id(1L).commodityType("MEDICINE").status("IN_TRANSIT").build();

        when(vehicleRepository.findByAssignedDriverUsername("driver")).thenReturn(Optional.of(vehicle));
        when(shipmentRepository.findByAssignedDriverUsername("driver")).thenReturn(List.of(shipment));

        ResponseEntity<DriverAssignmentDto> driverContext = mobileDriverController.getDriverContext(null);
        assertEquals(HttpStatus.OK, driverContext.getStatusCode());
        assertEquals("NER-07", driverContext.getBody().getAssignedVehicle().getCode());

        ResponseEntity<Vehicle> vehicleResp = mobileDriverController.getAssignedVehicle(null);
        assertEquals("NER-07", vehicleResp.getBody().getCode());

        ResponseEntity<List<Shipment>> shipmentResp = mobileDriverController.getAssignedShipments(null);
        assertEquals(1, shipmentResp.getBody().size());
    }

    @Test
    void testTrackingAndBatchLocationIngestion() {
        GpsLocationDto singleDto = GpsLocationDto.builder()
                .vehicleCode("NER-07")
                .latitude(25.1234)
                .longitude(92.5678)
                .speedKmh(45.0)
                .build();

        ResponseEntity<GpsLocationDto> singleResp = trackingController.ingestLocation(singleDto);
        assertEquals(HttpStatus.OK, singleResp.getStatusCode());
        assertEquals("NER-07", singleResp.getBody().getVehicleCode());

        BatchGpsLocationDto batchDto = BatchGpsLocationDto.builder()
                .vehicleCode("NER-07")
                .events(List.of(singleDto))
                .build();

        ResponseEntity<List<GpsLocationDto>> batchResp = trackingController.ingestBatchLocations(batchDto);
        assertEquals(HttpStatus.OK, batchResp.getStatusCode());
        assertEquals(1, batchResp.getBody().size());
    }

    @Test
    void testIncidentCreationAndEvidenceUpload() {
        CreateIncidentDto dto = CreateIncidentDto.builder()
                .type("LANDSLIDE")
                .reportedSeverity("CRITICAL")
                .latitude(25.1234)
                .longitude(92.5678)
                .build();

        Incident mockInc = Incident.builder().id(100L).type("LANDSLIDE").reportedSeverity("CRITICAL").build();
        when(incidentService.createIncident(any(), any())).thenReturn(mockInc);

        ResponseEntity<Incident> incResp = incidentController.createIncident(dto, null);
        assertEquals(HttpStatus.OK, incResp.getStatusCode());
        assertEquals(100L, incResp.getBody().getId());

        MockMultipartFile file = new MockMultipartFile("file", "debris.jpg", "image/jpeg", "fake image data".getBytes());
        FileUploadResponseDto uploadDto = FileUploadResponseDto.builder().fileUrl("/uploads/evidence/debris.jpg").build();
        doReturn(ResponseEntity.ok(uploadDto)).when(fileUploadController).uploadFile(file);

        ResponseEntity<?> fileResp = incidentController.uploadIncidentEvidence(100L, file);
        assertEquals(HttpStatus.OK, fileResp.getStatusCode());
    }

    @Test
    void testAccessibilityAndi18nExecution() {
        AccessibilityReportDto reportDto = AccessibilityReportDto.builder()
                .latitude(25.1234)
                .longitude(92.5678)
                .status("BLOCKED")
                .condition("LANDSLIDE")
                .corridorCode("COR-NH27")
                .build();

        Corridor mockCorridor = Corridor.builder().code("COR-NH27").status("BLOCKED").accessibilityScorePct(15.0).build();
        when(accessibilityEngineService.processAccessibilityReport(any(), any())).thenReturn(mockCorridor);

        ResponseEntity<Corridor> accResp = accessibilityController.submitAccessibilityReport(reportDto, null);
        assertEquals(HttpStatus.OK, accResp.getStatusCode());
        assertEquals("BLOCKED", accResp.getBody().getStatus());

        when(i18nService.getSupportedLanguages()).thenReturn(List.of(new LanguageDto("as", "Assamese", "অসমীয়া", false)));
        ResponseEntity<List<LanguageDto>> langResp = i18nController.getSupportedLanguages();
        assertEquals(1, langResp.getBody().size());
        assertEquals("as", langResp.getBody().get(0).getCode());
    }
}
