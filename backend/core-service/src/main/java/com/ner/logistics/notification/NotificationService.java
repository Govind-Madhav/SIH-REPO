package com.ner.logistics.notification;

import com.ner.logistics.incident.Incident;
import com.ner.logistics.incident.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final IncidentRepository incidentRepository;

    public List<NotificationDto> getPrioritizedNotifications() {
        List<Incident> activeIncidents = incidentRepository.findByStatus("ACTIVE");
        List<NotificationDto> notifications = new ArrayList<>();
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        if (!activeIncidents.isEmpty()) {
            notifications.add(NotificationDto.builder()
                    .id(1L)
                    .priority("CRITICAL")
                    .title("🚨 CRITICAL DISRUPTION: Haflong Pass Landslide")
                    .message("Landslide debris on NH-27 Guwahati-Silchar. Essential medicine convoy NER-07 affected.")
                    .targetRecipients(List.of("ADMIN", "LOGISTICS_OPERATOR", "DRIVER_NER07", "DISTRICT_AUTHORITY"))
                    .timestamp(timeNow)
                    .build());

            notifications.add(NotificationDto.builder()
                    .id(2L)
                    .priority("HIGH")
                    .title("⚠️ WEATHER ALERT: Dima Hasao Sector")
                    .message("Torrential rainfall intensity exceeding 135mm/24h. Route risk elevated to HIGH.")
                    .targetRecipients(List.of("LOGISTICS_OPERATOR", "NEARBY_CONVOY_DRIVERS"))
                    .timestamp(timeNow)
                    .build());
        }

        notifications.add(NotificationDto.builder()
                .id(3L)
                .priority("LOW")
                .title("ℹ️ ROUTE UPDATE: Umrangso Bypass")
                .message("Alternative bypass corridor operational with moderate travel time adjustment (+25 mins).")
                .targetRecipients(List.of("DASHBOARD_METRICS"))
                .timestamp(timeNow)
                .build());

        return notifications;
    }
}
