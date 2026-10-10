package com.tara.notification.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseTenantEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;

    @Builder.Default
    @Column(name = "notification_type", nullable = false)
    private String notificationType = "SYSTEM_UPDATE";

    @Builder.Default
    @Column(name = "severity", nullable = false)
    private String severity = "INFO"; // INFO, WARNING, ERROR, CRITICAL

    @Column(name = "link_url")
    private String linkUrl;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private Instant readAt;
}
