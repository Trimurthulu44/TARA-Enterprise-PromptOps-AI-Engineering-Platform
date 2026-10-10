package com.tara.org.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "workspaces")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workspace extends BaseTenantEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
}
