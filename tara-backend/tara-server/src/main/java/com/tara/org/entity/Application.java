package com.tara.org.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application extends BaseTenantEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "app_type", nullable = false)
    private String appType = "RAG";

    @Builder.Default
    @Column(name = "environment", nullable = false)
    private String environment = "DEVELOPMENT";

    @Builder.Default
    @Column(name = "latency_sla_ms")
    private Integer latencySlaMs = 2000;

    @Builder.Default
    @Column(name = "cost_budget_monthly")
    private BigDecimal costBudgetMonthly = BigDecimal.valueOf(500.0);

    @Column(name = "created_by")
    private UUID createdBy;
}
