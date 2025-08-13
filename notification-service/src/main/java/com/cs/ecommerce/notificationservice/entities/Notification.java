package com.cs.ecommerce.notificationservice.entities;

import com.cs.ecommerce.notificationservice.enums.NotificationStatus;
import com.cs.ecommerce.notificationservice.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_notification",
indexes = {
        @Index(name = "idx_notification01", columnList = "user_id"),
        @Index(name = "idx_notification02", columnList = "status"),
        @Index(name = "idx_notification03", columnList = "type")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq_gen")
    @SequenceGenerator(name = "notification_seq_gen", sequenceName = "seq_notification",
            allocationSize = 1)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(length = 255)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(length = 255, nullable = false)
    private String recipientEmail;

    private LocalDateTime sentAt;

    private LocalDateTime readAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
