package com.tara.org.controller;

import com.tara.common.dto.ApiResponse;
import com.tara.org.dto.OrgDtos;
import com.tara.org.entity.ApiKey;
import com.tara.org.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/api-keys")
@Tag(name = "API Keys", description = "High-security programmatic API keys for SDK and runtime integration")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping
    @Operation(summary = "List all API keys for tenant (hashes masked)")
    public ResponseEntity<ApiResponse<List<ApiKey>>> getApiKeys() {
        return ResponseEntity.ok(ApiResponse.ok(apiKeyService.getApiKeys()));
    }

    @PostMapping
    @Operation(summary = "Generate a new programmatic API key (raw secret displayed once)")
    public ResponseEntity<ApiResponse<OrgDtos.GeneratedApiKeyResponse>> createApiKey(
            @Valid @RequestBody OrgDtos.CreateApiKeyRequest request) {
        OrgDtos.GeneratedApiKeyResponse response = apiKeyService.createApiKey(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("API Key generated successfully. Save the rawKey securely as it will not be shown again.", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revoke an API key")
    public ResponseEntity<ApiResponse<Void>> revokeApiKey(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "Revoked by user") String reason) {
        apiKeyService.revokeApiKey(id, reason);
        return ResponseEntity.ok(ApiResponse.ok("API Key successfully revoked", null));
    }
}
