package com.ner.logistics.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;

    private String priority; // LOW, HIGH, CRITICAL

    private String title;

    private String message;

    private List<String> targetRecipients; // Dashboard, Logistics Operator, Driver, Admin, Emergency Authorities

    private String timestamp;
}
