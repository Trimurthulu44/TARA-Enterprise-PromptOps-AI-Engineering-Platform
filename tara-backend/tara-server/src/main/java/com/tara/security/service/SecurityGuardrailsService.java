package com.tara.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tara.common.context.TenantContextHolder;
import com.tara.security.dto.SecurityDtos;
import com.tara.security.entity.CanaryToken;
import com.tara.security.entity.SecurityScan;
import com.tara.security.repository.CanaryTokenRepository;
import com.tara.security.repository.SecurityScanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SecurityGuardrailsService {

    private final SecurityScanRepository securityScanRepository;
    private final CanaryTokenRepository canaryTokenRepository;
    private final ObjectMapper objectMapper;

    // Prompt Injection Patterns
    private static final List<Pattern> INJECTION_PATTERNS = List.of(
        Pattern.compile("(?i)ignore\\s+(all\\s+)?(previous|prior|above)\\s+instructions?"),
        Pattern.compile("(?i)disregard\\s+(all\\s+)?(earlier|previous|prior)\\s+(rules|directives|instructions)"),
        Pattern.compile("(?i)(act\\s+as|pretend\\s+you\\s+are|you\\s+are\\s+now)\\s+(DAN|unbound|jailbreak|developer\\s+mode|unrestricted)"),
        Pattern.compile("(?i)(reveal|show|print|output|dump)\\s+(your|the)?\\s*(system|initial|secret)\\s*(prompt|instructions)"),
        Pattern.compile("(?i)(repeat\\s+everything|what\\s+is\\s+written)\\s+(above|before\\s+this\\s+line)"),
        Pattern.compile("(?i)\\[\\s*SYSTEM\\s*\\]|<\\s*\\/?\\s*SYS\\s*>|---\\s*SYSTEM\\s+INSTRUCTION\\s*---")
    );

    // PII Regex Patterns
    private static final Pattern PII_EMAIL = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
    private static final Pattern PII_SSN = Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b");
    private static final Pattern PII_CREDIT_CARD = Pattern.compile("\\b(?:\\d{4}[ -]?){3}\\d{4}\\b");
    private static final Pattern PII_PHONE = Pattern.compile("\\b(?:\\+?1[-. ]?)?\\(?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})\\b");
    private static final Pattern PII_API_KEY = Pattern.compile("\\b(AKIA[0-9A-Z]{16}|sk-[a-zA-Z0-9]{24,}|ghp_[a-zA-Z0-9]{36}|tara_live_[a-f0-9]{48})\\b");

    public SecurityGuardrailsService(
        SecurityScanRepository securityScanRepository,
        CanaryTokenRepository canaryTokenRepository,
        ObjectMapper objectMapper
    ) {
        this.securityScanRepository = securityScanRepository;
        this.canaryTokenRepository = canaryTokenRepository;
        this.objectMapper = objectMapper;
    }

    public List<SecurityScan> getRecentScans() {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return securityScanRepository.findByOrganizationIdOrderByScannedAtDesc(orgId);
    }

    @Transactional
    public SecurityDtos.SecurityScanReport scanText(SecurityDtos.ScanPromptRequest request) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);

        String text = request.getText();
        List<String> violations = new ArrayList<>();
        List<SecurityDtos.PiiEntity> piiEntities = new ArrayList<>();

        // 1. Scan for Prompt Injection
        boolean injectionDetected = false;
        double injectionScore = 0.0;
        for (Pattern pattern : INJECTION_PATTERNS) {
            Matcher m = pattern.matcher(text);
            if (m.find()) {
                injectionDetected = true;
                injectionScore = Math.max(injectionScore, 0.92);
                violations.add("Prompt Injection Pattern Detected: " + m.group(0));
            }
        }

        // 2. Scan for PII
        String maskedText = text;
        maskedText = scanAndMask(PII_EMAIL, maskedText, "EMAIL", "[EMAIL_REDACTED]", piiEntities);
        maskedText = scanAndMask(PII_SSN, maskedText, "SSN", "[SSN_REDACTED]", piiEntities);
        maskedText = scanAndMask(PII_CREDIT_CARD, maskedText, "CREDIT_CARD", "[CREDIT_CARD_REDACTED]", piiEntities);
        maskedText = scanAndMask(PII_PHONE, maskedText, "PHONE", "[PHONE_REDACTED]", piiEntities);
        maskedText = scanAndMask(PII_API_KEY, maskedText, "API_KEY", "[SECRET_KEY_REDACTED]", piiEntities);

        boolean piiDetected = !piiEntities.isEmpty();
        if (piiDetected) {
            violations.add("PII Detected: " + piiEntities.size() + " sensitive entities identified and masked.");
        }

        // 3. Canary Token Leakage Scan
        boolean leakageDetected = false;
        if (request.getPromptVersionId() != null) {
            List<CanaryToken> canaries = canaryTokenRepository.findByPromptVersionId(request.getPromptVersionId());
            for (CanaryToken canary : canaries) {
                if (text.contains(canary.getTokenValue())) {
                    leakageDetected = true;
                    violations.add("Canary Token Leakage: System prompt canary [" + canary.getDescription() + "] found in output!");
                    canary.setTriggeredCount(canary.getTriggeredCount() + 1);
                    canary.setLastTriggeredAt(Instant.now());
                    canaryTokenRepository.save(canary);
                }
            }
        }

        // Determine Risk Severity
        String severity = "NONE";
        if (injectionDetected || leakageDetected) {
            severity = "CRITICAL";
        } else if (piiDetected) {
            severity = "HIGH";
        }

        boolean safe = violations.isEmpty();

        String recommendations = safe ? "All security guardrail checks passed successfully. Zero policy violations."
            : "Implement strict system prompt anchoring, isolate user input in clearly bounded XML/Markdown blocks, and enforce PII redaction.";

        SecurityScan scan = SecurityScan.builder()
            .projectId(request.getProjectId())
            .promptVersionId(request.getPromptVersionId())
            .scanTargetType(request.getTargetType())
            .scannedText(text.length() > 2000 ? text.substring(0, 2000) + "...[truncated]" : text)
            .isSafe(safe)
            .riskSeverity(severity)
            .injectionConfidence(BigDecimal.valueOf(injectionScore).setScale(4, RoundingMode.HALF_UP))
            .leakageConfidence(leakageDetected ? BigDecimal.ONE : BigDecimal.ZERO)
            .piiEntitiesDetected(writeJson(piiEntities))
            .violationsDetected(writeJson(violations))
            .remediationRecommendations(recommendations)
            .scannedBy(userId)
            .scannedAt(Instant.now())
            .build();
        scan.setOrganizationId(orgId);
        scan = securityScanRepository.save(scan);

        return SecurityDtos.SecurityScanReport.builder()
            .scanId(scan.getId())
            .safe(safe)
            .riskSeverity(severity)
            .injectionDetected(injectionDetected)
            .injectionConfidence(BigDecimal.valueOf(injectionScore).setScale(4, RoundingMode.HALF_UP))
            .leakageDetected(leakageDetected)
            .piiDetected(piiDetected)
            .piiEntities(piiEntities)
            .violations(violations)
            .maskedText(maskedText)
            .recommendations(recommendations)
            .build();
    }

    private String scanAndMask(Pattern pattern, String text, String type, String replacement, List<SecurityDtos.PiiEntity> entities) {
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            entities.add(SecurityDtos.PiiEntity.builder()
                .type(type)
                .matchedValue(m.group(0))
                .start(m.start())
                .end(m.end())
                .build());
        }
        return m.replaceAll(replacement);
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }
}
