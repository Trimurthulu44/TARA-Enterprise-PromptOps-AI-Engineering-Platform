package com.tara.prompt.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deployment_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentHistory extends BaseTenantEntity {

    @Column(name = "prompt_id", nullable = false)
    private UUID promptId;

    @Column(name = "prompt_version_id", nullable = false)
    private UUID promptVersionId;

    @Column(name = "environment", nullable = false)
    private String environment; // STAGING, PRODUCTION

    @Column(name = "previous_version_id")
    private UUID previousVersionId;

    @Builder.Default
    @Column(name = "deployment_type", nullable = false)
    private String deploymentType = "PROMOTION"; // PROMOTION, ROLLBACK

    @Column(name = "deployment_notes")
    private String deploymentNotes;

    @Column(name = "deployed_by")
    private UUID deployedBy;

    @Builder.Default
    @Column(name = "deployed_at", nullable = false)
    private Instant deployedAt = Instant.now();
}
