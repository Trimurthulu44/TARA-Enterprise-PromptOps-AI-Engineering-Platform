package com.tara.prompt.controller;

import com.tara.common.dto.ApiResponse;
import com.tara.prompt.dto.PromptDtos;
import com.tara.prompt.entity.Prompt;
import com.tara.prompt.entity.PromptVersion;
import com.tara.prompt.service.PromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prompts")
@Tag(name = "Prompts", description = "Enterprise prompt lifecycle, version control, rendering, promotion, and rollback APIs")
public class PromptController {

    private final PromptService promptService;

    public PromptController(PromptService promptService) {
        this.promptService = promptService;
    }

    @GetMapping
    @Operation(summary = "List or search prompts in current workspace")
    public ResponseEntity<ApiResponse<List<Prompt>>> getPrompts(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(promptService.getPrompts(projectId, search)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prompt details and current active deployment pointers")
    public ResponseEntity<ApiResponse<Prompt>> getPromptById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(promptService.getPromptById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new prompt catalog item with initial v1.0.0 semantic version")
    public ResponseEntity<ApiResponse<Prompt>> createPrompt(@Valid @RequestBody PromptDtos.CreatePromptRequest request) {
        Prompt prompt = promptService.createPrompt(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Prompt created successfully with version v1.0.0", prompt));
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "Get immutable version history for a prompt")
    public ResponseEntity<ApiResponse<List<PromptVersion>>> getVersionHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(promptService.getVersionHistory(id)));
    }

    @GetMapping("/{id}/versions/{versionSemver}")
    @Operation(summary = "Get specific semantic version of a prompt")
    public ResponseEntity<ApiResponse<PromptVersion>> getVersion(
            @PathVariable UUID id,
            @PathVariable String versionSemver) {
        return ResponseEntity.ok(ApiResponse.ok(promptService.getVersion(id, versionSemver)));
    }

    @PostMapping("/{id}/versions")
    @Operation(summary = "Commit a new immutable semantic version of a prompt")
    public ResponseEntity<ApiResponse<PromptVersion>> createVersion(
            @PathVariable UUID id,
            @Valid @RequestBody PromptDtos.CreatePromptVersionRequest request) {
        PromptVersion version = promptService.createVersion(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("New version committed: " + version.getVersionSemver(), version));
    }

    @PostMapping("/{id}/promote")
    @Operation(summary = "Promote a prompt version to Staging or Production environment")
    public ResponseEntity<ApiResponse<Prompt>> promoteVersion(
            @PathVariable UUID id,
            @Valid @RequestBody PromptDtos.PromoteVersionRequest request) {
        Prompt prompt = promptService.promoteVersion(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Prompt successfully promoted to " + request.getEnvironment(), prompt));
    }

    @PostMapping("/{id}/rollback")
    @Operation(summary = "Instant one-click rollback of environment pointer to previous stable version")
    public ResponseEntity<ApiResponse<Prompt>> rollback(
            @PathVariable UUID id,
            @Valid @RequestBody PromptDtos.RollbackRequest request) {
        Prompt prompt = promptService.rollback(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Prompt successfully rolled back to " + request.getTargetVersionSemver(), prompt));
    }

    @PostMapping("/render")
    @Operation(summary = "Render prompt template with variable substitutions for runtime AI execution")
    public ResponseEntity<ApiResponse<PromptDtos.RenderedPromptResponse>> renderPrompt(
            @Valid @RequestBody PromptDtos.RenderPromptRequest request) {
        PromptDtos.RenderedPromptResponse response = promptService.renderPrompt(request);
        return ResponseEntity.ok(ApiResponse.ok("Prompt rendered successfully", response));
    }
}
