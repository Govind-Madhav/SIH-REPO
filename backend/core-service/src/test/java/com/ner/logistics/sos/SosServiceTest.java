package com.ner.logistics.sos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SosServiceTest {

    @Mock
    private SosEventRepository sosEventRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private SosService sosService;

    @BeforeEach
    void setUp() {
        sosService = new SosService(sosEventRepository, messagingTemplate);
    }

    @Test
    void testProcessRelayedSos() {
        SosRelayRequestDto relayDto = SosRelayRequestDto.builder()
                .meshPacketId("SOS-MESH-NER07-99881")
                .originVehicleCode("NER-07")
                .originLatitude(25.1234)
                .originLongitude(92.5678)
                .emergencyType("LANDSLIDE_TRAPPED")
                .message("Trapped in Haflong pass zero connectivity valley")
                .originTimestamp(LocalDateTime.now().minusMinutes(20))
                .relayedByVehicleCode("NER-02")
                .hopCount(1)
                .build();

        when(sosEventRepository.save(any(SosEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SosEvent result = sosService.processRelayedSos(relayDto);

        assertNotNull(result);
        assertEquals("NER-07", result.getVehicleCode());
        assertEquals("MESH_RELAY_STORE_FORWARD", result.getDeliveryType());
        assertEquals("NER-02", result.getRelayedByVehicle());
        assertEquals(1, result.getRelayHopCount());
        assertTrue(result.getRelayLatencyMinutes() >= 19);
    }
}
