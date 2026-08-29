package com.ner.logistics.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackingKafkaProducer {

    public static final String TOPIC_VEHICLE_LOCATION = "vehicle.location.updated";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishLocationUpdate(GpsLocationDto dto) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(TOPIC_VEHICLE_LOCATION, dto.getVehicleCode(), jsonPayload);
            log.info("Published location update to Kafka topic [{}]: vehicleCode={}", TOPIC_VEHICLE_LOCATION, dto.getVehicleCode());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize GpsLocationDto for Kafka", e);
        }
    }
}
