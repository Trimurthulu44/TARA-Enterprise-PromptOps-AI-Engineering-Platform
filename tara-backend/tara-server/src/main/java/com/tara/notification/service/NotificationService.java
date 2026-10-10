package com.tara.notification.service;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.exception.ResourceNotFoundException;
import com.tara.notification.entity.Notification;
import com.tara.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotifications() {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return notificationRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId);
    }

    @Transactional
    public void markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        notification.setIsRead(true);
        notification.setReadAt(Instant.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotification(UUID userId, String title, String message, String type, String severity, String link) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        Notification n = Notification.builder()
            .userId(userId)
            .title(title)
            .message(message)
            .notificationType(type)
            .severity(severity)
            .linkUrl(link)
            .isRead(false)
            .build();
        n.setOrganizationId(orgId);
        return notificationRepository.save(n);
    }
}
