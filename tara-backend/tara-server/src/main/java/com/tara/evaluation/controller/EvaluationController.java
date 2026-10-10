package com.tara.evaluation.controller;

import com.tara.common.dto.ApiResponse;
import com.tara.evaluation.dto.EvaluationDtos;
import com.tara.evaluation.entity.EvaluationResult;
import com.tara.evaluation.entity.EvaluationRun;
import com.tara.evaluation.entity.RegressionReport;
import com.tara.evaluation.service.EvaluationService;
import com.tara.evaluation.service.RegressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluations")
@Tag(name = "Evaluations", description = "Evaluation runs, LLM-as-a-judge scoring, and regression testing APIs")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final RegressionService regressionService;

    public EvaluationController(EvaluationService evaluationService, RegressionService regressionService) {
        this.evaluationService = evaluationService;
        this.regressionService = regressionService;
    }

    @GetMapping("/runs")
    @Operation(summary = "List all evaluation runs")
    public ResponseEntity<ApiResponse<List<EvaluationRun>>> getRuns() {
        return ResponseEntity.ok(ApiResponse.ok(evaluationService.getEvaluationRuns()));
    }

    @GetMapping("/runs/{id}")
    @Operation(summary = "Get evaluation run summary")
    public ResponseEntity<ApiResponse<EvaluationRun>> getRunById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(evaluationService.getEvaluationRunById(id)));
    }

    @GetMapping("/runs/{id}/results")
    @Operation(summary = "Get detailed test case results for an evaluation run")
    public ResponseEntity<ApiResponse<List<EvaluationResult>>> getRunResults(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(evaluationService.getEvaluationResults(id)));
    }

    @PostMapping("/runs")
    @Operation(summary = "Trigger a new batch evaluation run")
    public ResponseEntity<ApiResponse<EvaluationRun>> triggerEvaluation(
            @Valid @RequestBody EvaluationDtos.TriggerEvaluationRequest request) {
        EvaluationRun run = evaluationService.triggerEvaluation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Evaluation run completed successfully", run));
    }

    @GetMapping("/regressions")
    @Operation(summary = "List regression test comparison reports")
    public ResponseEntity<ApiResponse<List<RegressionReport>>> getRegressionReports() {
        return ResponseEntity.ok(ApiResponse.ok(regressionService.getRegressionReports()));
    }

    @PostMapping("/regressions")
    @Operation(summary = "Trigger side-by-side regression testing comparing candidate vs baseline versions")
    public ResponseEntity<ApiResponse<RegressionReport>> triggerRegression(
            @Valid @RequestBody EvaluationDtos.TriggerRegressionRequest request) {
        RegressionReport report = regressionService.triggerRegressionTest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Regression comparison test completed", report));
    }
}
