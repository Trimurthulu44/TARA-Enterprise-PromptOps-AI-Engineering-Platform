package com.tara.org.controller;

import com.tara.common.dto.ApiResponse;
import com.tara.org.dto.OrgDtos;
import com.tara.org.entity.Application;
import com.tara.org.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
@Tag(name = "Applications", description = "Target AI applications registered for PromptOps monitoring and governance")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    @Operation(summary = "List all registered AI applications")
    public ResponseEntity<ApiResponse<List<Application>>> getApplications(
            @RequestParam(required = false) UUID projectId) {
        if (projectId != null) {
            return ResponseEntity.ok(ApiResponse.ok(applicationService.getApplicationsByProject(projectId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getApplications()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application details by ID")
    public ResponseEntity<ApiResponse<Application>> getApplicationById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getApplicationById(id)));
    }

    @PostMapping
    @Operation(summary = "Register a new AI application")
    public ResponseEntity<ApiResponse<Application>> createApplication(@Valid @RequestBody OrgDtos.CreateApplicationRequest request) {
        Application app = applicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Application registered successfully", app));
    }
}
