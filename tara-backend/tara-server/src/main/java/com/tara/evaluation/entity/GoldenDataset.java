package com.tara.evaluation.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "golden_datasets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldenDataset extends BaseTenantEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private String version = "v1.0.0";

    @Builder.Default
    @Column(name = "total_test_cases", nullable = false)
    private Integer totalTestCases = 0;

    @Column(name = "tags")
    private String tags;

    @Column(name = "created_by")
    private UUID createdBy;
}
