package com.tara.org.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseTenantEntity {

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "created_by")
    private UUID createdBy;
}
