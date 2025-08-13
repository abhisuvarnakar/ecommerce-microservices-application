package com.cs.ecommerce.notificationservice.dto;

import com.cs.ecommerce.notificationservice.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationResponseDTO {

    private Long notificationId;
    private NotificationStatus status;
}
