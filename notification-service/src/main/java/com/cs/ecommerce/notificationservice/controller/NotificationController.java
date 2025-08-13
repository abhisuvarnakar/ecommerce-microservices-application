package com.cs.ecommerce.notificationservice.controller;

import com.cs.ecommerce.notificationservice.dto.EmailNotificationRequestDTO;
import com.cs.ecommerce.notificationservice.dto.EmailNotificationResponseDTO;
import com.cs.ecommerce.notificationservice.dto.NotificationResponseDTO;
import com.cs.ecommerce.notificationservice.enums.NotificationStatus;
import com.cs.ecommerce.notificationservice.enums.NotificationType;
import com.cs.ecommerce.notificationservice.service.NotificationService;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    public ResponseEntity<ApiResponse<EmailNotificationResponseDTO>> sendEmailNotification(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid EmailNotificationRequestDTO request) {
        EmailNotificationResponseDTO response = notificationService.sendEmailNotification(userId, request);
        if (!response.getStatus().equals(NotificationStatus.SENT)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(response, "Failed to send email notification"));
        }
        return ResponseEntity.ok(ApiResponse.success(response, "Email notification queued successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> getUserNotifications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "type", required = false) NotificationType type,
            @RequestParam(name = "read", required = false) Boolean read) {
        NotificationResponseDTO response = notificationService.getUserNotifications(userId, page, size, type, read);
        return ResponseEntity.ok(ApiResponse.success(response, "User notifications fetched successfully"));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<MessageResponse>> markAsRead(
            @PathVariable("notificationId") Long notificationId) {
        MessageResponse response = notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(response, "Notification marked as read"));
    }

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<Set<String>>> getTemplates() {
        Set<String> templates = notificationService.getAvailableTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates, "Notification templates fetched successfully"));
    }
}