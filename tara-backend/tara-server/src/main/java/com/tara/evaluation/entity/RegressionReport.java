package com.tara.evaluation.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "regression_reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegressionReport extends BaseTenantEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "candidate_version_id", nullable = false)
    private UUID candidateVersionId;

    @Column(name = "baseline_version_id", nullable = false)
    private UUID baselineVersionId;

    @Column(name = "candidate_run_id")
    private UUID candidateRunId;

    @Column(name = "baseline_run_id")
    private UUID baselineRunId;

    @Column(name = "candidate_avg_score", nullable = false)
    private BigDecimal candidateAvgScore;

    @Column(name = "baseline_avg_score", nullable = false)
    private BigDecimal baselineAvgScore;

    @Column(name = "delta_score", nullable = false)
    private BigDecimal deltaScore; // candidate - baseline

    @Column(name = "candidate_p95_latency_ms", nullable = false)
    private Integer candidateP95LatencyMs;

    @Column(name = "baseline_p95_latency_ms", nullable = false)
    private Integer baselineP95LatencyMs;

    @Column(name = "delta_latency_ms", nullable = false)
    private Integer deltaLatencyMs;

    @Builder.Default
    @Column(name = "regression_detected", nullable = false)
    private Boolean regressionDetected = false;

    @Builder.Default
    @Column(name = "quality_gate_passed", nullable = false)
    private Boolean qualityGatePassed = true;

    @Column(name = "summary_report", columnDefinition = "text")
    private String summaryReport;
}
