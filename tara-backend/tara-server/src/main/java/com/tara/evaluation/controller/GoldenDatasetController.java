package com.tara.evaluation.controller;

import com.tara.common.dto.ApiResponse;
import com.tara.evaluation.dto.EvaluationDtos;
import com.tara.evaluation.entity.GoldenDataset;
import com.tara.evaluation.entity.GoldenTestCase;
import com.tara.evaluation.service.GoldenDatasetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/datasets")
@Tag(name = "Golden Datasets", description = "Golden datasets and curated ground-truth test case management")
public class GoldenDatasetController {

    private final GoldenDatasetService datasetService;

    public GoldenDatasetController(GoldenDatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping
    @Operation(summary = "List golden datasets")
    public ResponseEntity<ApiResponse<List<GoldenDataset>>> getDatasets(@RequestParam(required = false) UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(datasetService.getDatasets(projectId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get golden dataset details")
    public ResponseEntity<ApiResponse<GoldenDataset>> getDatasetById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(datasetService.getDatasetById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new golden dataset suite")
    public ResponseEntity<ApiResponse<GoldenDataset>> createDataset(@Valid @RequestBody EvaluationDtos.CreateDatasetRequest request) {
        GoldenDataset dataset = datasetService.createDataset(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Golden dataset created successfully", dataset));
    }

    @GetMapping("/{id}/test-cases")
    @Operation(summary = "Get all test cases for a golden dataset")
    public ResponseEntity<ApiResponse<List<GoldenTestCase>>> getTestCases(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(datasetService.getTestCases(id)));
    }

    @PostMapping("/{id}/test-cases")
    @Operation(summary = "Add an individual test case to a golden dataset")
    public ResponseEntity<ApiResponse<GoldenTestCase>> addTestCase(
            @PathVariable UUID id,
            @Valid @RequestBody EvaluationDtos.CreateTestCaseRequest request) {
        GoldenTestCase testCase = datasetService.addTestCase(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Test case added successfully", testCase));
    }

    @PostMapping("/{id}/test-cases/batch")
    @Operation(summary = "Batch import test cases into a golden dataset")
    public ResponseEntity<ApiResponse<String>> batchImport(
            @PathVariable UUID id,
            @Valid @RequestBody EvaluationDtos.BatchImportTestCasesRequest request) {
        int count = datasetService.batchImport(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Imported " + count + " test cases successfully", null));
    }
}
