package com.tara.security.controller;

import com.tara.common.dto.ApiResponse;
import com.tara.security.dto.SecurityDtos;
import com.tara.security.entity.SecurityScan;
import com.tara.security.service.SecurityGuardrailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/security")
@Tag(name = "Security & Guardrails", description = "Prompt injection detection, canary leakage checks, and PII masking APIs")
public class SecurityController {

    private final SecurityGuardrailsService securityGuardrailsService;

    public SecurityController(SecurityGuardrailsService securityGuardrailsService) {
        this.securityGuardrailsService = securityGuardrailsService;
    }

    @PostMapping("/scan")
    @Operation(summary = "Perform real-time security scanning (Prompt Injection, Canary Leakage, PII Redaction)")
    public ResponseEntity<ApiResponse<SecurityDtos.SecurityScanReport>> scanText(
            @Valid @RequestBody SecurityDtos.ScanPromptRequest request) {
        SecurityDtos.SecurityScanReport report = securityGuardrailsService.scanText(request);
        return ResponseEntity.ok(ApiResponse.ok("Security analysis complete", report));
    }

    @GetMapping("/scans")
    @Operation(summary = "Get historical security scans and threat detection logs")
    public ResponseEntity<ApiResponse<List<SecurityScan>>> getRecentScans() {
        return ResponseEntity.ok(ApiResponse.ok(securityGuardrailsService.getRecentScans()));
    }
}
