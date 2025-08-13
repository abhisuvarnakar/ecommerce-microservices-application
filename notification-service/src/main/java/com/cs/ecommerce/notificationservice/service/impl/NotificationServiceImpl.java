package com.cs.ecommerce.notificationservice.service.impl;

import com.cs.ecommerce.notificationservice.dto.EmailNotificationRequestDTO;
import com.cs.ecommerce.notificationservice.dto.EmailNotificationResponseDTO;
import com.cs.ecommerce.notificationservice.dto.NotificationDTO;
import com.cs.ecommerce.notificationservice.dto.NotificationResponseDTO;
import com.cs.ecommerce.notificationservice.entities.Notification;
import com.cs.ecommerce.notificationservice.enums.NotificationStatus;
import com.cs.ecommerce.notificationservice.enums.NotificationType;
import com.cs.ecommerce.notificationservice.repository.NotificationRepository;
import com.cs.ecommerce.notificationservice.service.EmailSenderService;
import com.cs.ecommerce.notificationservice.service.NotificationService;
import com.cs.ecommerce.notificationservice.templates.NotificationTemplateStore;
import com.cs.ecommerce.sharedmodules.dto.MessageResponse;
import com.cs.ecommerce.sharedmodules.dto.Pagination;
import com.cs.ecommerce.sharedmodules.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateStore templateStore;
    private final EmailSenderService emailSenderService;
    private final ModelMapper modelMapper;

    @Override
    public EmailNotificationResponseDTO sendEmailNotification(Long userId,
                                                              EmailNotificationRequestDTO req) {
        String templateText =
                templateStore.getTemplate(req.getTemplate()).orElseThrow(() -> new ResourceNotFoundException("Template not found: " + req.getTemplate()));
        String processedMessage = processTemplate(templateText, req.getData());

        Notification notification = Notification.builder()
                .userId(userId)
                .type(NotificationType.valueOf(req.getTemplate()))
                .status(NotificationStatus.PENDING)
                .subject(req.getSubject())
                .message(processedMessage)
                .recipientEmail(req.getTo())
                .createdAt(LocalDateTime.now())
                .build();

        notification = notificationRepository.save(notification);

        boolean sent = sendEmail(notification);
        notification.setStatus(sent ? NotificationStatus.SENT : NotificationStatus.FAILED);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return new EmailNotificationResponseDTO(notification.getId(), notification.getStatus());
    }

    @Override
    public String processTemplate(String template, Map<String, Object> data) {
        String result = template;
        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }
        }
        return result;
    }

    @Override
    public boolean sendEmail(Notification notification) {
        log.info("Sending email to {} with message: {}", notification.getRecipientEmail(),
                notification.getMessage());
        try {
            emailSenderService.sendEmail(notification.getRecipientEmail(),
                    notification.getSubject(), notification.getMessage());
            return true;
        } catch (Exception e) {
            notification.setErrorMessage(e.getMessage());
        }
        return false;
    }

    @Override
    public NotificationResponseDTO getUserNotifications(Long userId, int page, int size,
                                                        NotificationType type, Boolean read) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> pageResult = notificationRepository.findByUserIdAndFilters(userId, type, read, pageable);

        List<NotificationDTO> notifications = pageResult.stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());

        Pagination pagination = new Pagination(
                page, size, pageResult.getTotalElements(), pageResult.getTotalPages()
        );

        return new NotificationResponseDTO(notifications, pagination);
    }

    @Override
    public MessageResponse markNotificationAsRead(Long notificationId) {
        Notification notification =
                notificationRepository.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return new MessageResponse("Notification marked as read");
    }

    @Override
    public Set<String> getAvailableTemplates() {
        return templateStore.getAvailableTemplates();
    }
}
