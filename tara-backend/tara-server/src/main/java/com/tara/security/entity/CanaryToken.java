package com.tara.security.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "canary_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanaryToken extends BaseTenantEntity {

    @Column(name = "prompt_version_id", nullable = false)
    private UUID promptVersionId;

    @Column(name = "token_value", nullable = false, unique = true)
    private String tokenValue;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "triggered_count", nullable = false)
    private Integer triggeredCount = 0;

    @Column(name = "last_triggered_at")
    private Instant lastTriggeredAt;
}
