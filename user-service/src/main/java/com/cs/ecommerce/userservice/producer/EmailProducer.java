package com.cs.ecommerce.userservice.producer;

import com.cs.ecommerce.sharedmodules.dto.email.EmailNotificationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProducer {

    private final KafkaTemplate<String, EmailNotificationRequestDTO> kafkaTemplate;

    @Value("${kafka.topic.email-out}")
    private String EMAIL_OUT_TOPIC;

    public void sendEmailEvent(EmailNotificationRequestDTO emailRequestDTO) {
        kafkaTemplate.send(EMAIL_OUT_TOPIC, emailRequestDTO);
    }
}
