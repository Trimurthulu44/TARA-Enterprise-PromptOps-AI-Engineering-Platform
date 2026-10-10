package com.tara.evaluation.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "evaluation_runs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRun extends BaseTenantEntity {

    @Column(name = "prompt_version_id", nullable = false)
    private UUID promptVersionId;

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "run_name", nullable = false)
    private String runName;

    @Builder.Default
    @Column(name = "run_type", nullable = false)
    private String runType = "AUTOMATED_BENCHMARK"; // AUTOMATED_BENCHMARK, REGRESSION_CHECK, LLM_JUDGE

    @Builder.Default
    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, RUNNING, COMPLETED, FAILED

    @Builder.Default
    @Column(name = "judge_model")
    private String judgeModel = "gpt-4o";

    @Builder.Default
    @Column(name = "total_cases", nullable = false)
    private Integer totalCases = 0;

    @Builder.Default
    @Column(name = "completed_cases", nullable = false)
    private Integer completedCases = 0;

    @Builder.Default
    @Column(name = "passed_cases", nullable = false)
    private Integer passedCases = 0;

    @Builder.Default
    @Column(name = "failed_cases", nullable = false)
    private Integer failedCases = 0;

    @Builder.Default
    @Column(name = "average_score")
    private BigDecimal averageScore = BigDecimal.ZERO;

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
    @Column(name = "total_tokens_used")
    private Integer totalTokensUsed = 0;

    @Builder.Default
    @Column(name = "total_cost_usd")
    private BigDecimal totalCostUsd = BigDecimal.ZERO;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "initiated_by")
    private UUID initiatedBy;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
