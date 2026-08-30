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

    public List<NotificationDto> getPrioritizedNotifications(String lang) {
        String language = (lang != null && "hi".equalsIgnoreCase(lang)) ? "hi" : "en";
        List<Incident> activeIncidents = incidentRepository.findByStatus("ACTIVE");
        List<NotificationDto> notifications = new ArrayList<>();
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        if (!activeIncidents.isEmpty()) {
            if ("hi".equalsIgnoreCase(language)) {
                notifications.add(NotificationDto.builder()
                        .id(1L)
                        .priority("CRITICAL")
                        .title("🚨 गंभीर बाधा: हाफलोंग पास भूस्खलन")
                        .message("एनएच-27 गुवाहाटी-सिलचर मार्ग पर भूस्खलन का मलबा। आवश्यक दवा कॉन्वॉय NER-07 प्रभावित।")
                        .languageCode("hi")
                        .targetRecipients(List.of("ADMIN", "LOGISTICS_OPERATOR", "DRIVER_NER07", "DISTRICT_AUTHORITY"))
                        .timestamp(timeNow)
                        .build());

                notifications.add(NotificationDto.builder()
                        .id(2L)
                        .priority("HIGH")
                        .title("⚠️ मौसम चेतावनी: डिमा हसाओ सेक्टर")
                        .message("मूसलाधार बारिश की तीव्रता 135mm/24h से अधिक। मार्ग जोखिम उच्च स्तर पर।")
                        .languageCode("hi")
                        .targetRecipients(List.of("LOGISTICS_OPERATOR", "NEARBY_CONVOY_DRIVERS"))
                        .timestamp(timeNow)
                        .build());
            } else {
                notifications.add(NotificationDto.builder()
                        .id(1L)
                        .priority("CRITICAL")
                        .title("🚨 CRITICAL DISRUPTION: Haflong Pass Landslide")
                        .message("Landslide debris on NH-27 Guwahati-Silchar. Essential medicine convoy NER-07 affected.")
                        .languageCode("en")
                        .targetRecipients(List.of("ADMIN", "LOGISTICS_OPERATOR", "DRIVER_NER07", "DISTRICT_AUTHORITY"))
                        .timestamp(timeNow)
                        .build());

                notifications.add(NotificationDto.builder()
                        .id(2L)
                        .priority("HIGH")
                        .title("⚠️ WEATHER ALERT: Dima Hasao Sector")
                        .message("Torrential rainfall intensity exceeding 135mm/24h. Route risk elevated to HIGH.")
                        .languageCode("en")
                        .targetRecipients(List.of("LOGISTICS_OPERATOR", "NEARBY_CONVOY_DRIVERS"))
                        .timestamp(timeNow)
                        .build());
            }
        }

        if ("hi".equalsIgnoreCase(language)) {
            notifications.add(NotificationDto.builder()
                    .id(3L)
                    .priority("LOW")
                    .title("ℹ️ मार्ग अद्यतन: उमरांगसो बाईपास")
                    .message("वैकल्पिक बाईपास गलियारा चालू है (+25 मिनट यात्रा समय समायोजन)।")
                    .languageCode("hi")
                    .targetRecipients(List.of("DASHBOARD_METRICS"))
                    .timestamp(timeNow)
                    .build());
        } else {
            notifications.add(NotificationDto.builder()
                    .id(3L)
                    .priority("LOW")
                    .title("ℹ️ ROUTE UPDATE: Umrangso Bypass")
                    .message("Alternative bypass corridor operational with moderate travel time adjustment (+25 mins).")
                    .languageCode("en")
                    .targetRecipients(List.of("DASHBOARD_METRICS"))
                    .timestamp(timeNow)
                    .build());
        }

        return notifications;
    }
}
