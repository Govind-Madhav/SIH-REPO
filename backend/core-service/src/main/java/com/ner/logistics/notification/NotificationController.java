package com.ner.logistics.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/prioritized")
    public ResponseEntity<List<NotificationDto>> getPrioritizedNotifications() {
        return ResponseEntity.ok(notificationService.getPrioritizedNotifications());
    }
}
