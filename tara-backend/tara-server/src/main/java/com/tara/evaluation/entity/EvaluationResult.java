package com.tara.evaluation.entity;

import com.tara.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "evaluation_results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult extends BaseEntity {

    @Column(name = "evaluation_run_id", nullable = false)
    private UUID evaluationRunId;

    @Column(name = "test_case_id", nullable = false)
    private UUID testCaseId;

    @Column(name = "rendered_prompt", nullable = false, columnDefinition = "text")
    private String renderedPrompt;

    @Column(name = "model_output", columnDefinition = "text")
    private String modelOutput;

    @Builder.Default
    @Column(name = "latency_ms", nullable = false)
    private Integer latencyMs = 0;

    @Builder.Default
    @Column(name = "prompt_tokens", nullable = false)
    private Integer promptTokens = 0;

    @Builder.Default
    @Column(name = "completion_tokens", nullable = false)
    private Integer completionTokens = 0;

    @Builder.Default
    @Column(name = "cost_usd")
    private BigDecimal costUsd = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "judge_score")
    private BigDecimal judgeScore = BigDecimal.ZERO;

    @Column(name = "judge_reasoning", columnDefinition = "text")
    private String judgeReasoning;

    @Column(name = "rubric_scores", columnDefinition = "text")
    private String rubricScores;

    @Builder.Default
    @Column(name = "passed", nullable = false)
    private Boolean passed = true;

    @Column(name = "failure_reason")
    private String failureReason;
}
