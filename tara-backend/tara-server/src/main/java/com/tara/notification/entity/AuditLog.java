package com.tara.notification.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseTenantEntity {

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "actor_email", nullable = false)
    private String actorEmail;

    @Column(name = "action", nullable = false)
    private String action; // PROMPT_CREATED, VERSION_PROMOTED, QUALITY_GATE_TRIGGERED

    @Column(name = "resource_type", nullable = false)
    private String resourceType; // PROMPT, VERSION, API_KEY, DATASET

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "diff_json", columnDefinition = "text")
    private String diffJson;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;
}
