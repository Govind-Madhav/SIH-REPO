package com.ner.logistics.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestParam(required = false, defaultValue = "en") String lang) {
        return ResponseEntity.ok(notificationService.getPrioritizedNotifications(lang));
    }
}
