package com.tara.security.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_scans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityScan extends BaseTenantEntity {

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "prompt_version_id")
    private UUID promptVersionId;

    @Column(name = "scan_target_type", nullable = false)
    private String scanTargetType; // PROMPT_TEMPLATE, RENDERED_INPUT, MODEL_OUTPUT

    @Column(name = "scanned_text", nullable = false, columnDefinition = "text")
    private String scannedText;

    @Builder.Default
    @Column(name = "is_safe", nullable = false)
    private Boolean isSafe = true;

    @Builder.Default
    @Column(name = "risk_severity", nullable = false)
    private String riskSeverity = "NONE"; // NONE, LOW, MEDIUM, HIGH, CRITICAL

    @Builder.Default
    @Column(name = "injection_confidence")
    private BigDecimal injectionConfidence = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "leakage_confidence")
    private BigDecimal leakageConfidence = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "pii_entities_detected", columnDefinition = "text")
    private String piiEntitiesDetected = "[]";

    @Builder.Default
    @Column(name = "violations_detected", columnDefinition = "text")
    private String violationsDetected = "[]";

    @Column(name = "remediation_recommendations", columnDefinition = "text")
    private String remediationRecommendations;

    @Column(name = "scanned_by")
    private UUID scannedBy;

    @Builder.Default
    @Column(name = "scanned_at", nullable = false)
    private Instant scannedAt = Instant.now();
}
