package com.cs.ecommerce.notificationservice.service;

import com.cs.ecommerce.notificationservice.dto.EmailNotificationRequestDTO;
import com.cs.ecommerce.notificationservice.dto.EmailNotificationResponseDTO;
import com.cs.ecommerce.notificationservice.dto.NotificationResponseDTO;
import com.cs.ecommerce.notificationservice.entities.Notification;
import com.cs.ecommerce.notificationservice.enums.NotificationType;
import com.cs.ecommerce.sharedmodules.dto.MessageResponse;

import java.util.Map;
import java.util.Set;

public interface NotificationService {

    EmailNotificationResponseDTO sendEmailNotification(Long userId, EmailNotificationRequestDTO req);

    String processTemplate(String template, Map<String, Object> data);

    boolean sendEmail(Notification notification);

    NotificationResponseDTO getUserNotifications(Long userId, int page, int size,
                                                 NotificationType type, Boolean read);

    MessageResponse markNotificationAsRead(Long notificationId);

    Set<String> getAvailableTemplates();
}
