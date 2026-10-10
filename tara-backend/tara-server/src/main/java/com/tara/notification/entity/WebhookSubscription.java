package com.tara.notification.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "webhook_subscriptions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookSubscription extends BaseTenantEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @Column(name = "secret_hash", nullable = false)
    private String secretHash;

    @Column(name = "event_types", nullable = false)
    private String eventTypes; // Comma-separated: "evaluation.failed,security.violation,prompt.promoted"

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "consecutive_failures", nullable = false)
    private Integer consecutiveFailures = 0;
}
