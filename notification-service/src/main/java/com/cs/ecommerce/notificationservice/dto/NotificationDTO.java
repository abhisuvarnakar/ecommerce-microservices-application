package com.cs.ecommerce.notificationservice.dto;

import com.cs.ecommerce.notificationservice.enums.NotificationStatus;
import com.cs.ecommerce.notificationservice.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;
    private NotificationType type;
    private NotificationStatus status;
    private String subject;
    private String message;
    private String recipientEmail;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
