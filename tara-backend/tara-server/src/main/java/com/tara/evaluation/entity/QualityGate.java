package com.tara.evaluation.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "quality_gates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityGate extends BaseTenantEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "min_judge_score")
    private BigDecimal minJudgeScore = BigDecimal.valueOf(0.85);

    @Builder.Default
    @Column(name = "max_p95_latency_ms")
    private Integer maxP95LatencyMs = 2000;

    @Builder.Default
    @Column(name = "max_cost_per_run_usd")
    private BigDecimal maxCostPerRunUsd = BigDecimal.valueOf(10.0);

    @Builder.Default
    @Column(name = "require_zero_security_violations")
    private Boolean requireZeroSecurityViolations = true;

    @Builder.Default
    @Column(name = "min_pass_rate_percentage")
    private BigDecimal minPassRatePercentage = BigDecimal.valueOf(95.0);

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
