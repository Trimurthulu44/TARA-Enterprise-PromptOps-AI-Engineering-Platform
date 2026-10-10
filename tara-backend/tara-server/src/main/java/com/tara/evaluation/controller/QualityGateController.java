package com.tara.evaluation.controller;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.dto.ApiResponse;
import com.tara.evaluation.entity.QualityGate;
import com.tara.evaluation.repository.QualityGateRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quality-gates")
@Tag(name = "Quality Gates", description = "Automated deployment governance rules and quality gates")
public class QualityGateController {

    private final QualityGateRepository qualityGateRepository;

    public QualityGateController(QualityGateRepository qualityGateRepository) {
        this.qualityGateRepository = qualityGateRepository;
    }

    @GetMapping
    @Operation(summary = "List quality gates configured for workspace")
    public ResponseEntity<ApiResponse<List<QualityGate>>> getQualityGates(@RequestParam(required = false) UUID projectId) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        if (projectId != null) {
            return ResponseEntity.ok(ApiResponse.ok(qualityGateRepository.findByProjectId(projectId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(qualityGateRepository.findByOrganizationId(orgId)));
    }

    @PostMapping
    @Operation(summary = "Create an automated quality gate rule")
    public ResponseEntity<ApiResponse<QualityGate>> createQualityGate(@Valid @RequestBody QualityGate gate) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        gate.setOrganizationId(orgId);
        QualityGate saved = qualityGateRepository.save(gate);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Quality gate created successfully", saved));
    }
}
