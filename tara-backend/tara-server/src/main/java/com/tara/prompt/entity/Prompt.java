package com.tara.prompt.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prompts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prompt extends BaseTenantEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "category")
    private String category = "GENERAL";

    @Column(name = "tags")
    private String tags; // Comma-separated tags

    @Column(name = "current_production_version_id")
    private UUID currentProductionVersionId;

    @Column(name = "current_staging_version_id")
    private UUID currentStagingVersionId;

    @Column(name = "created_by")
    private UUID createdBy;
}
