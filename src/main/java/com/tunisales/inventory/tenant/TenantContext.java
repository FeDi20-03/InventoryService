package com.tunisales.inventory.tenant;

import java.util.UUID;

/**
 * Thread-local holder for the current tenant identifier.
 *
 * <p>Set by {@link TenantInterceptor} from the {@code X-Tenant-Id} request
 * header and cleared after every request to prevent context leaks between
 * pooled threads.</p>
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    /**
     * Returns the current tenant UUID, or {@code null} if none is set.
     */
    public static UUID get() {
        return CURRENT_TENANT.get();
    }

    /**
     * Sets the current tenant UUID.
     */
    public static void set(UUID tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Clears the current tenant context.
     * Must be called after every request (typically in the interceptor's afterCompletion).
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
