package com.tunisales.inventory.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.tunisales.inventory.service.dto.NotificationPayloadDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

/**
 * Unit tests for {@link PlatformNotificationClient}, exercising the real
 * {@code RestTemplate} against an in-process WireMock server. Verifies:
 * <ul>
 *   <li>The published JSON body matches the {@link NotificationPayloadDTO} schema.</li>
 *   <li>The endpoint URL is built correctly (handles trailing slash).</li>
 *   <li>Transport failures are swallowed (must NOT throw).</li>
 *   <li>{@code null} payload is a no-op.</li>
 * </ul>
 */
class PlatformNotificationClientTest {

    private static WireMockServer wireMock;

    @BeforeAll
    static void startServer() {
        wireMock = new WireMockServer(options().dynamicPort());
        wireMock.start();
    }

    @AfterAll
    static void stopServer() {
        if (wireMock != null) {
            wireMock.stop();
        }
    }

    @BeforeEach
    void resetStubs() {
        wireMock.resetAll();
    }

    private PlatformNotificationClient newClient(String baseUrl) {
        return new PlatformNotificationClient(new RestTemplateBuilder(), baseUrl);
    }

    @Test
    void publishInternal_postsExpectedJsonToCorrectEndpoint() {
        // Arrange
        wireMock.stubFor(post(urlEqualTo("/api/notifications/publish")).willReturn(aResponse().withStatus(204)));
        PlatformNotificationClient client = newClient(wireMock.baseUrl());
        NotificationPayloadDTO payload = new NotificationPayloadDTO(
            "*MAGASINIER*",
            "AUDIT_SCHEDULED",
            "Stock audit scheduled",
            "Body",
            "{\"auditId\":42}"
        );

        // Act
        client.publishInternal(payload);

        // Assert
        wireMock.verify(
            postRequestedFor(urlEqualTo("/api/notifications/publish"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.type", equalTo("AUDIT_SCHEDULED")))
                .withRequestBody(matchingJsonPath("$.recipientLogin", equalTo("*MAGASINIER*")))
                .withRequestBody(matchingJsonPath("$.title", equalTo("Stock audit scheduled")))
                .withRequestBody(matchingJsonPath("$.body", equalTo("Body")))
                .withRequestBody(matchingJsonPath("$.payloadJson", equalTo("{\"auditId\":42}")))
        );
    }

    @Test
    void publishInternal_trimsTrailingSlashOnBaseUrl() {
        // Arrange
        wireMock.stubFor(post(urlEqualTo("/api/notifications/publish")).willReturn(aResponse().withStatus(200)));
        PlatformNotificationClient client = newClient(wireMock.baseUrl() + "/");
        NotificationPayloadDTO payload = new NotificationPayloadDTO("u", "T", "ti", "b", null);

        // Act
        client.publishInternal(payload);

        // Assert: served by the same stub — no extra "//"-prefixed path required.
        wireMock.verify(postRequestedFor(urlEqualTo("/api/notifications/publish")));
    }

    @Test
    void publishInternal_swallowsServerErrorWithoutThrowing() {
        // Arrange
        wireMock.stubFor(post(urlEqualTo("/api/notifications/publish")).willReturn(serverError()));
        PlatformNotificationClient client = newClient(wireMock.baseUrl());
        NotificationPayloadDTO payload = new NotificationPayloadDTO("u", "T", null, null, null);

        // Act + Assert (does not throw)
        client.publishInternal(payload);

        wireMock.verify(postRequestedFor(urlEqualTo("/api/notifications/publish")));
    }

    @Test
    void publishInternal_swallowsConnectionFailure() {
        // Arrange — point at a port nobody listens on.
        PlatformNotificationClient client = newClient("http://localhost:1");
        NotificationPayloadDTO payload = new NotificationPayloadDTO("u", "T", null, null, null);

        // Act + Assert (must not throw)
        client.publishInternal(payload);
    }

    @Test
    void publishInternal_isNoOpForNullPayload() {
        // Arrange
        wireMock.stubFor(post(urlEqualTo("/api/notifications/publish")).willReturn(aResponse().withStatus(204)));
        PlatformNotificationClient client = newClient(wireMock.baseUrl());

        // Act
        client.publishInternal(null);

        // Assert — no HTTP call at all.
        wireMock.verify(0, postRequestedFor(urlEqualTo("/api/notifications/publish")));
    }

    @Test
    void publishInternal_handlesEmptyBaseUrlGracefully() {
        // Arrange — defensive: trimTrailingSlash must accept null/empty (private method
        // exercised indirectly by an empty baseUrl, which would produce "/api/...").
        PlatformNotificationClient client = newClient("");
        NotificationPayloadDTO payload = new NotificationPayloadDTO("u", "T", null, null, null);

        // Act + Assert — must not throw (RestTemplate will fail and exception is swallowed).
        client.publishInternal(payload);
    }
}
