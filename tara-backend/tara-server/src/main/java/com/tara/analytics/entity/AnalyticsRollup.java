package com.tara.analytics.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "analytics_rollups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRollup extends BaseTenantEntity {

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "prompt_id")
    private UUID promptId;

    @Column(name = "prompt_version_id")
    private UUID promptVersionId;

    @Column(name = "bucket_timestamp", nullable = false)
    private Instant bucketTimestamp;

    @Builder.Default
    @Column(name = "interval_type", nullable = false)
    private String intervalType = "DAY"; // HOUR, DAY, MONTH

    @Builder.Default
    @Column(name = "total_requests", nullable = false)
    private Integer totalRequests = 0;

    @Builder.Default
    @Column(name = "successful_requests", nullable = false)
    private Integer successfulRequests = 0;

    @Builder.Default
    @Column(name = "failed_requests", nullable = false)
    private Integer failedRequests = 0;

    @Builder.Default
    @Column(name = "total_tokens", nullable = false)
    private Long totalTokens = 0L;

    @Builder.Default
    @Column(name = "prompt_tokens", nullable = false)
    private Long promptTokens = 0L;

    @Builder.Default
    @Column(name = "completion_tokens", nullable = false)
    private Long completionTokens = 0L;

    @Builder.Default
    @Column(name = "avg_latency_ms")
    private BigDecimal avgLatencyMs = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "p50_latency_ms")
    private Integer p50LatencyMs = 0;

    @Builder.Default
    @Column(name = "p95_latency_ms")
    private Integer p95LatencyMs = 0;

    @Builder.Default
    @Column(name = "p99_latency_ms")
    private Integer p99LatencyMs = 0;

    @Builder.Default
    @Column(name = "total_cost_usd")
    private BigDecimal totalCostUsd = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "avg_evaluation_score")
    private BigDecimal avgEvaluationScore = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "security_violations_count", nullable = false)
    private Integer securityViolationsCount = 0;
}
