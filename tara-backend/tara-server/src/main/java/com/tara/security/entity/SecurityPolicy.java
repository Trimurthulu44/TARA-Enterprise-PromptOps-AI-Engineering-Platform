package com.tara.security.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "security_policies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityPolicy extends BaseTenantEntity {

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "name", nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "enable_prompt_injection_detection", nullable = false)
    private Boolean enablePromptInjectionDetection = true;

    @Builder.Default
    @Column(name = "enable_leakage_detection", nullable = false)
    private Boolean enableLeakageDetection = true;

    @Builder.Default
    @Column(name = "enable_pii_masking", nullable = false)
    private Boolean enablePiiMasking = true;

    @Builder.Default
    @Column(name = "enable_unsafe_output_check", nullable = false)
    private Boolean enableUnsafeOutputCheck = true;

    @Builder.Default
    @Column(name = "severity_threshold", nullable = false)
    private String severityThreshold = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL

    @Builder.Default
    @Column(name = "action_on_violation", nullable = false)
    private String actionOnViolation = "BLOCK_AND_ALERT"; // LOG_ONLY, ALERT_ONLY, BLOCK_AND_ALERT, MASK_AND_PROCEED

    @Column(name = "custom_forbidden_regexes")
    private String customForbiddenRegexes;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
}
