package com.tara.org.controller;

import com.tara.common.dto.ApiResponse;
import com.tara.org.dto.OrgDtos;
import com.tara.org.entity.Project;
import com.tara.org.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Project grouping and management for AI workloads")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "List all projects in the current tenant workspace")
    public ResponseEntity<ApiResponse<List<Project>>> getProjects() {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProjects()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project details by ID")
    public ResponseEntity<ApiResponse<Project>> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProjectById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ApiResponse<Project>> createProject(@Valid @RequestBody OrgDtos.CreateProjectRequest request) {
        Project project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Project created successfully", project));
    }
}
