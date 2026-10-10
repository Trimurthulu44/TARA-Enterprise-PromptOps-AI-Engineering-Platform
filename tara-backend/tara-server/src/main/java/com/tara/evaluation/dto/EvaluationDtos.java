package com.tara.evaluation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class EvaluationDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDatasetRequest {
        @NotNull(message = "Project ID is required")
        private UUID projectId;

        @NotBlank(message = "Dataset name is required")
        private String name;

        private String description;
        private String tags;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTestCaseRequest {
        private String testCaseName;

        @NotBlank(message = "Input variables JSON is required")
        private String inputVariables; // e.g. {"customer_query": "How do I reset my API key?"}

        private String expectedOutput;
        private String groundTruthMetadata;
        private String assertionRules;
        private BigDecimal weight;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchImportTestCasesRequest {
        private List<CreateTestCaseRequest> testCases;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TriggerEvaluationRequest {
        @NotNull(message = "Prompt version ID is required")
        private UUID promptVersionId;

        @NotNull(message = "Dataset ID is required")
        private UUID datasetId;

        private String runName;
        private String judgeModel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TriggerRegressionRequest {
        @NotNull(message = "Candidate version ID is required")
        private UUID candidateVersionId;

        @NotNull(message = "Baseline version ID is required")
        private UUID baselineVersionId;

        @NotNull(message = "Dataset ID is required")
        private UUID datasetId;

        private UUID qualityGateId;
    }
}
