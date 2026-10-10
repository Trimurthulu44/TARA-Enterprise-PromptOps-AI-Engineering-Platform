package com.tara.org.controller;

import com.tara.common.dto.ApiResponse;
import com.tara.org.dto.OrgDtos;
import com.tara.org.entity.Organization;
import com.tara.org.entity.Workspace;
import com.tara.org.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations")
@Tag(name = "Organizations", description = "Multi-tenant organization settings, quotas, and workspace management")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/current")
    @Operation(summary = "Get current organization details and quotas")
    public ResponseEntity<ApiResponse<Organization>> getCurrentOrganization() {
        return ResponseEntity.ok(ApiResponse.ok(organizationService.getCurrentOrganization()));
    }

    @GetMapping("/workspaces")
    @Operation(summary = "List workspaces belonging to the current organization")
    public ResponseEntity<ApiResponse<List<Workspace>>> getWorkspaces() {
        return ResponseEntity.ok(ApiResponse.ok(organizationService.getWorkspaces()));
    }

    @PostMapping("/workspaces")
    @Operation(summary = "Create a new workspace within the current organization")
    public ResponseEntity<ApiResponse<Workspace>> createWorkspace(@Valid @RequestBody OrgDtos.CreateWorkspaceRequest request) {
        Workspace ws = organizationService.createWorkspace(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Workspace created successfully", ws));
    }
}
