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
    private String priority;
    private String title;
    private String message;
    private String languageCode;
    private List<String> targetRecipients;
    private String timestamp;
}
