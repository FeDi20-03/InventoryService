package com.tunisales.inventory.tenant;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Spring MVC {@link HandlerInterceptor} that reads the {@code X-Tenant-Id}
 * header (set by the Gateway's {@code TenantHeaderFilter}) and populates
 * {@link TenantContext} for the duration of the request.
 *
 * <p>Registered in {@link com.tunisales.inventory.config.WebConfigurer}.</p>
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    public static final String TENANT_HEADER = "X-Tenant-Id";

    private final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String tenantHeader = request.getHeader(TENANT_HEADER);
        if (StringUtils.hasText(tenantHeader)) {
            try {
                UUID tenantId = UUID.fromString(tenantHeader);
                TenantContext.set(tenantId);
                log.debug("TenantContext set to {}", tenantId);
            } catch (IllegalArgumentException e) {
                log.warn("Received invalid X-Tenant-Id header value '{}', ignoring", tenantHeader);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler,
        Exception ex
    ) {
        TenantContext.clear();
    }
}
