package com.tara.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SecurityDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScanPromptRequest {
        @NotBlank(message = "Text to scan is required")
        private String text;

        private UUID promptVersionId;
        private UUID projectId;

        @Builder.Default
        private String targetType = "RENDERED_INPUT"; // PROMPT_TEMPLATE, RENDERED_INPUT, MODEL_OUTPUT
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecurityScanReport {
        private UUID scanId;
        private boolean safe;
        private String riskSeverity; // NONE, LOW, MEDIUM, HIGH, CRITICAL
        private boolean injectionDetected;
        private BigDecimal injectionConfidence;
        private boolean leakageDetected;
        private boolean piiDetected;
        private List<PiiEntity> piiEntities;
        private List<String> violations;
        private String maskedText;
        private String recommendations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PiiEntity {
        private String type; // EMAIL, PHONE, SSN, CREDIT_CARD, API_KEY
        private String matchedValue;
        private int start;
        private int end;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCanaryTokenRequest {
        private UUID promptVersionId;
        private String description;
    }
}
