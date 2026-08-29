package com.ner.logistics.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackingKafkaConsumer {

    private final RedisTrackingService redisTrackingService;
    private final VehicleLocationRepository vehicleLocationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @KafkaListener(topics = TrackingKafkaProducer.TOPIC_VEHICLE_LOCATION, groupId = "ner-logistics-group")
    public void consumeLocationUpdate(String message) {
        try {
            GpsLocationDto dto = objectMapper.readValue(message, GpsLocationDto.class);
            if (dto.getTimestamp() == null) {
                dto.setTimestamp(LocalDateTime.now());
            }

            // 1. Update Redis ephemeral cache
            redisTrackingService.saveLatestLocation(dto);

            // 2. Persist spatial PostGIS history in PostgreSQL
            Point spatialPoint = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
            spatialPoint.setSRID(4326);

            VehicleLocation locationEntity = VehicleLocation.builder()
                    .vehicleCode(dto.getVehicleCode())
                    .location(spatialPoint)
                    .latitude(dto.getLatitude())
                    .longitude(dto.getLongitude())
                    .speedKmh(dto.getSpeedKmh())
                    .headingDegrees(dto.getHeadingDegrees())
                    .timestamp(dto.getTimestamp())
                    .build();

            vehicleLocationRepository.save(locationEntity);

            // 3. Broadcast to WebSockets
            messagingTemplate.convertAndSend("/topic/vehicle-telemetry", dto);
            log.info("Kafka Consumer processed & broadcasted GPS update for vehicle: {}", dto.getVehicleCode());

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Kafka GPS location payload", e);
        }
    }
}
