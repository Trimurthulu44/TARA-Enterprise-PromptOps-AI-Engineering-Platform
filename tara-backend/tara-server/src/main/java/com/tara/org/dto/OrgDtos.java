package com.tara.org.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrgDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWorkspaceRequest {
        @NotBlank(message = "Workspace name is required")
        private String name;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProjectRequest {
        @NotBlank(message = "Project name is required")
        private String name;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateApplicationRequest {
        private UUID projectId;
        @NotBlank(message = "Application name is required")
        private String name;
        private String description;
        private String appType; // RAG, AGENT, CHATBOT
        private String environment; // DEVELOPMENT, STAGING, PRODUCTION
        private Integer latencySlaMs;
        private BigDecimal costBudgetMonthly;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateApiKeyRequest {
        @NotBlank(message = "Key name is required")
        private String name;
        private UUID applicationId;
        private String scopes;
        private Integer rateLimitPerMinute;
        private Instant expiresAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedApiKeyResponse {
        private UUID id;
        private String name;
        private String rawKey; // Presented once to developer
        private String keyPrefix;
        private Instant createdAt;
        private Instant expiresAt;
    }
}
