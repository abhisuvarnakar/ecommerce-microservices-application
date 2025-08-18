package com.cs.ecommerce.notificationservice.consumer;

import com.cs.ecommerce.notificationservice.service.NotificationService;
import com.cs.ecommerce.sharedmodules.dto.email.EmailNotificationRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.email-out}", groupId = "email-service-group")
    public void consumeEmailEvent(EmailNotificationRequestDTO emailRequest) {
        try {
            log.info("Received email notification request for: {}", emailRequest.getTo());
            Long userId = Long.parseLong(Objects.toString(emailRequest.getData().get("userId")));
            notificationService.sendEmailNotification(userId, emailRequest);
        } catch (Exception e) {
            log.error("Failed to process email notification: {}", emailRequest, e);
        }
    }
}
