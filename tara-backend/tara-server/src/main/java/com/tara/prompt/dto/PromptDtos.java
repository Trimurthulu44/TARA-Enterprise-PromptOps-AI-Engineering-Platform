package com.tara.prompt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class PromptDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePromptRequest {
        @NotNull(message = "Project ID is required")
        private UUID projectId;

        @NotBlank(message = "Prompt name is required")
        private String name;

        private String description;
        private String category;
        private String tags;

        private String systemPrompt;

        @NotBlank(message = "User prompt template is required")
        private String userPromptTemplate;

        private String variableSchema; // JSON string

        @Builder.Default
        private String modelProvider = "OPENAI";

        @Builder.Default
        private String modelName = "gpt-4o";

        @Builder.Default
        private BigDecimal temperature = BigDecimal.valueOf(0.70);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePromptVersionRequest {
        @NotBlank(message = "Commit message is required")
        private String commitMessage;

        private String systemPrompt;

        @NotBlank(message = "User prompt template is required")
        private String userPromptTemplate;

        private String variableSchema;
        private String modelProvider;
        private String modelName;
        private BigDecimal temperature;
        private BigDecimal topP;
        private Integer maxTokens;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RenderPromptRequest {
        @NotNull(message = "Prompt ID is required")
        private UUID promptId;

        // Either semver e.g. "v1.0.0", or "production", or "staging"
        private String version;

        private Map<String, Object> variables;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RenderedPromptResponse {
        private UUID promptId;
        private String promptName;
        private String versionSemver;
        private String systemPrompt;
        private String renderedUserPrompt;
        private Integer estimatedTokens;
        private String modelProvider;
        private String modelName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromoteVersionRequest {
        @NotBlank(message = "Version semver is required")
        private String versionSemver;

        @NotBlank(message = "Target environment is required (STAGING or PRODUCTION)")
        private String environment;

        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RollbackRequest {
        @NotBlank(message = "Target version semver is required")
        private String targetVersionSemver;

        @NotBlank(message = "Target environment is required (STAGING or PRODUCTION)")
        private String environment;

        private String reason;
    }
}
