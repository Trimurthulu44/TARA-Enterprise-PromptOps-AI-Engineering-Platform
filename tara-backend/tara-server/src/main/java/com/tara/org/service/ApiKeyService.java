package com.tara.org.service;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.exception.ResourceNotFoundException;
import com.tara.common.security.ApiKeyUtil;
import com.tara.org.dto.OrgDtos;
import com.tara.org.entity.ApiKey;
import com.tara.org.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public List<ApiKey> getApiKeys() {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return apiKeyRepository.findByOrganizationId(orgId);
    }

    @Transactional
    public OrgDtos.GeneratedApiKeyResponse createApiKey(OrgDtos.CreateApiKeyRequest request) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        UUID workspaceId = TenantContextHolder.getRequiredWorkspaceId();
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);

        ApiKeyUtil.GeneratedKey gen = ApiKeyUtil.generateKey();

        ApiKey key = ApiKey.builder()
            .workspaceId(workspaceId)
            .applicationId(request.getApplicationId())
            .name(request.getName().trim())
            .keyPrefix(gen.prefix())
            .keyHash(gen.keyHash())
            .scopes(request.getScopes() != null ? request.getScopes() : "prompts:read,evaluations:run")
            .rateLimitPerMinute(request.getRateLimitPerMinute() != null ? request.getRateLimitPerMinute() : 600)
            .expiresAt(request.getExpiresAt())
            .isRevoked(false)
            .createdBy(userId)
            .build();
        key.setOrganizationId(orgId);
        key = apiKeyRepository.save(key);

        return OrgDtos.GeneratedApiKeyResponse.builder()
            .id(key.getId())
            .name(key.getName())
            .rawKey(gen.rawKey())
            .keyPrefix(gen.prefix())
            .createdAt(key.getCreatedAt())
            .expiresAt(key.getExpiresAt())
            .build();
    }

    @Transactional
    public void revokeApiKey(UUID keyId, String reason) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        ApiKey key = apiKeyRepository.findById(keyId)
            .filter(k -> k.getOrganizationId().equals(orgId))
            .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));

        key.setIsRevoked(true);
        key.setRevokedAt(Instant.now());
        key.setRevokedReason(reason != null ? reason : "Revoked by user");
        apiKeyRepository.save(key);
    }
}
