package com.tunisales.inventory.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Unit tests for {@link TenantInterceptor}.
 *
 * <p>The interceptor reads the {@code X-Tenant-Id} header set by the gateway and
 * propagates it via the thread-local {@link TenantContext}. These tests
 * exercise the parsing, error-handling and afterCompletion cleanup contract
 * directly with mock servlet objects (no Spring context required).</p>
 */
class TenantInterceptorTest {

    private TenantInterceptor interceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private final Object handler = new Object();

    @BeforeEach
    void setUp() {
        interceptor = new TenantInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void preHandle_setsTenantContextWhenHeaderIsValidUuid() {
        // Arrange
        UUID expected = UUID.fromString("11111111-2222-3333-4444-555555555555");
        request.addHeader(TenantInterceptor.TENANT_HEADER, expected.toString());

        // Act
        boolean proceed = interceptor.preHandle((HttpServletRequest) request, (HttpServletResponse) response, handler);

        // Assert
        assertThat(proceed).isTrue();
        assertThat(TenantContext.get()).isEqualTo(expected);
    }

    @Test
    void preHandle_ignoresInvalidUuidHeaderAndLeavesContextNull() {
        // Arrange
        request.addHeader(TenantInterceptor.TENANT_HEADER, "not-a-uuid");

        // Act
        boolean proceed = interceptor.preHandle(request, response, handler);

        // Assert
        assertThat(proceed).isTrue();
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void preHandle_doesNothingWhenHeaderIsMissing() {
        // Arrange — no header

        // Act
        boolean proceed = interceptor.preHandle(request, response, handler);

        // Assert
        assertThat(proceed).isTrue();
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void preHandle_doesNothingWhenHeaderIsBlank() {
        // Arrange
        request.addHeader(TenantInterceptor.TENANT_HEADER, "   ");

        // Act
        boolean proceed = interceptor.preHandle(request, response, handler);

        // Assert
        assertThat(proceed).isTrue();
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void afterCompletion_clearsTenantContextEvenAfterPreHandleSet() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);
        assertThat(TenantContext.get()).isEqualTo(tenantId);

        // Act
        interceptor.afterCompletion(request, response, handler, null);

        // Assert
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void afterCompletion_clearsTenantContextEvenWhenExceptionPresent() {
        // Arrange — simulate the request having set a tenant and then thrown.
        TenantContext.set(UUID.randomUUID());

        // Act
        interceptor.afterCompletion(request, response, handler, new RuntimeException("boom"));

        // Assert
        assertThat(TenantContext.get()).isNull();
    }
}
