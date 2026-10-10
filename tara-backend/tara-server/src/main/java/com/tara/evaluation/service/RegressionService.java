package com.tara.evaluation.service;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.exception.ResourceNotFoundException;
import com.tara.evaluation.dto.EvaluationDtos;
import com.tara.evaluation.entity.EvaluationRun;
import com.tara.evaluation.entity.QualityGate;
import com.tara.evaluation.entity.RegressionReport;
import com.tara.evaluation.repository.EvaluationRunRepository;
import com.tara.evaluation.repository.QualityGateRepository;
import com.tara.evaluation.repository.RegressionReportRepository;
import com.tara.prompt.entity.Prompt;
import com.tara.prompt.entity.PromptVersion;
import com.tara.prompt.repository.PromptRepository;
import com.tara.prompt.repository.PromptVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class RegressionService {

    private final RegressionReportRepository regressionReportRepository;
    private final EvaluationService evaluationService;
    private final QualityGateRepository qualityGateRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final PromptRepository promptRepository;
    private final EvaluationRunRepository evaluationRunRepository;

    public RegressionService(
        RegressionReportRepository regressionReportRepository,
        EvaluationService evaluationService,
        QualityGateRepository qualityGateRepository,
        PromptVersionRepository promptVersionRepository,
        PromptRepository promptRepository,
        EvaluationRunRepository evaluationRunRepository
    ) {
        this.regressionReportRepository = regressionReportRepository;
        this.evaluationService = evaluationService;
        this.qualityGateRepository = qualityGateRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.promptRepository = promptRepository;
        this.evaluationRunRepository = evaluationRunRepository;
    }

    public List<RegressionReport> getRegressionReports() {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return regressionReportRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId);
    }

    @Transactional
    public RegressionReport triggerRegressionTest(EvaluationDtos.TriggerRegressionRequest request) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();

        PromptVersion candidate = promptVersionRepository.findById(request.getCandidateVersionId())
            .filter(v -> v.getOrganizationId().equals(orgId))
            .orElseThrow(() -> new ResourceNotFoundException("Candidate PromptVersion", request.getCandidateVersionId()));

        PromptVersion baseline = promptVersionRepository.findById(request.getBaselineVersionId())
            .filter(v -> v.getOrganizationId().equals(orgId))
            .orElseThrow(() -> new ResourceNotFoundException("Baseline PromptVersion", request.getBaselineVersionId()));

        Prompt prompt = promptRepository.findById(candidate.getPromptId())
            .orElseThrow(() -> new ResourceNotFoundException("Prompt", candidate.getPromptId()));

        // Run evaluation on candidate
        EvaluationRun candidateRun = evaluationService.triggerEvaluation(EvaluationDtos.TriggerEvaluationRequest.builder()
            .promptVersionId(candidate.getId())
            .datasetId(request.getDatasetId())
            .runName("Regression Candidate: " + candidate.getVersionSemver())
            .build());

        // Run evaluation on baseline
        EvaluationRun baselineRun = evaluationService.triggerEvaluation(EvaluationDtos.TriggerEvaluationRequest.builder()
            .promptVersionId(baseline.getId())
            .datasetId(request.getDatasetId())
            .runName("Regression Baseline: " + baseline.getVersionSemver())
            .build());

        BigDecimal deltaScore = candidateRun.getAverageScore().subtract(baselineRun.getAverageScore());
        int deltaLatency = candidateRun.getP95LatencyMs() - baselineRun.getP95LatencyMs();

        // Quality Gate check
        QualityGate gate = null;
        if (request.getQualityGateId() != null) {
            gate = qualityGateRepository.findById(request.getQualityGateId()).orElse(null);
        }
        if (gate == null) {
            gate = qualityGateRepository.findFirstByProjectIdAndIsActiveTrue(prompt.getProjectId()).orElse(null);
        }

        boolean regression = deltaScore.compareTo(BigDecimal.valueOf(-0.02)) < 0 || deltaLatency > 300;
        boolean gatePassed = true;

        if (gate != null) {
            if (candidateRun.getAverageScore().compareTo(gate.getMinJudgeScore()) < 0) {
                gatePassed = false;
            }
            if (candidateRun.getP95LatencyMs() > gate.getMaxP95LatencyMs()) {
                gatePassed = false;
            }
        }

        String summary = String.format(
            "{\"candidateVersion\": \"%s\", \"baselineVersion\": \"%s\", \"candidateScore\": %s, \"baselineScore\": %s, \"deltaScore\": %s, \"candidateP95Latency\": %d, \"baselineP95Latency\": %d, \"deltaLatency\": %d, \"status\": \"%s\"}",
            candidate.getVersionSemver(),
            baseline.getVersionSemver(),
            candidateRun.getAverageScore(),
            baselineRun.getAverageScore(),
            deltaScore,
            candidateRun.getP95LatencyMs(),
            baselineRun.getP95LatencyMs(),
            deltaLatency,
            (!regression && gatePassed) ? "PASSED" : "REGRESSION_DETECTED"
        );

        RegressionReport report = RegressionReport.builder()
            .projectId(prompt.getProjectId())
            .datasetId(request.getDatasetId())
            .candidateVersionId(candidate.getId())
            .baselineVersionId(baseline.getId())
            .candidateRunId(candidateRun.getId())
            .baselineRunId(baselineRun.getId())
            .candidateAvgScore(candidateRun.getAverageScore())
            .baselineAvgScore(baselineRun.getAverageScore())
            .deltaScore(deltaScore)
            .candidateP95LatencyMs(candidateRun.getP95LatencyMs())
            .baselineP95LatencyMs(baselineRun.getP95LatencyMs())
            .deltaLatencyMs(deltaLatency)
            .regressionDetected(regression)
            .qualityGatePassed(gatePassed)
            .summaryReport(summary)
            .build();
        report.setOrganizationId(orgId);

        return regressionReportRepository.save(report);
    }
}
