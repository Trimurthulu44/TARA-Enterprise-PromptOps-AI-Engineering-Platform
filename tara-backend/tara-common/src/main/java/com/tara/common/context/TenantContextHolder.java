package com.tara.common.context;

import java.util.Optional;
import java.util.UUID;

/**
 * ThreadLocal container ensuring strict tenant isolation across execution threads.
 */
public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private TenantContextHolder() {}

    public static void setContext(TenantContext context) {
        CONTEXT.set(context);
    }

    public static Optional<TenantContext> getContext() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static UUID getRequiredOrganizationId() {
        return getContext()
            .map(TenantContext::organizationId)
            .orElseThrow(() -> new IllegalStateException("Operation requires active Organization context but none was found"));
    }

    public static UUID getRequiredWorkspaceId() {
        return getContext()
            .map(TenantContext::workspaceId)
            .orElseThrow(() -> new IllegalStateException("Operation requires active Workspace context but none was found"));
    }

    public static UUID getRequiredUserId() {
        return getContext()
            .map(TenantContext::userId)
            .orElseThrow(() -> new IllegalStateException("Operation requires authenticated User context but none was found"));
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
