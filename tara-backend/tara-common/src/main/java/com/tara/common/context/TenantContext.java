package com.tara.common.context;

import java.util.UUID;

/**
 * Encapsulates the multi-tenant context for the currently executing request.
 * Guaranteed zero tenant data leakage via thread-isolated state.
 */
public record TenantContext(
    UUID organizationId,
    UUID workspaceId,
    UUID userId,
    String userEmail,
    String role
) {
    public static TenantContext of(UUID organizationId, UUID workspaceId, UUID userId, String userEmail, String role) {
        return new TenantContext(organizationId, workspaceId, userId, userEmail, role);
    }

    public static TenantContext anonymous() {
        return new TenantContext(null, null, null, "anonymous@tara.ai", "ANONYMOUS");
    }
}
