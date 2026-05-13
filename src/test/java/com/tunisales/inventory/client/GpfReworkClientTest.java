package com.tunisales.inventory.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

/**
 * Unit tests for {@link GpfReworkClient} against an in-process WireMock server.
 */
class GpfReworkClientTest {

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

    private GpfReworkClient newClient() {
        return new GpfReworkClient(new RestTemplateBuilder(), wireMock.baseUrl() + "/api/rework");
    }

    @Test
    void sendRework_returnsExternalIdOn2xx() {
        // Arrange
        wireMock.stubFor(
            post(urlEqualTo("/api/rework"))
                .willReturn(
                    aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"GPF-789\",\"status\":\"received\"}")
                )
        );
        GpfReworkClient client = newClient();

        // Act
        Optional<String> externalId = client.sendRework("490154203237518", "screen broken");

        // Assert
        assertThat(externalId).contains("GPF-789");
        wireMock.verify(
            postRequestedFor(urlEqualTo("/api/rework"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.imei", equalTo("490154203237518")))
                .withRequestBody(matchingJsonPath("$.defectReason", equalTo("screen broken")))
        );
    }

    @Test
    void sendRework_returnsEmptyWhenResponseHasNoIdField() {
        // Arrange
        wireMock.stubFor(
            post(urlEqualTo("/api/rework"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{\"status\":\"queued\"}"))
        );
        GpfReworkClient client = newClient();

        // Act
        Optional<String> externalId = client.sendRework("490154203237518", null);

        // Assert
        assertThat(externalId).isEmpty();
        wireMock.verify(postRequestedFor(urlEqualTo("/api/rework")).withRequestBody(matchingJsonPath("$.defectReason", equalTo(""))));
    }

    @Test
    void sendRework_returnsEmptyOn5xxWithoutThrowing() {
        // Arrange
        wireMock.stubFor(post(urlEqualTo("/api/rework")).willReturn(serverError()));
        GpfReworkClient client = newClient();

        // Act
        Optional<String> externalId = client.sendRework("490154203237518", "panne");

        // Assert
        assertThat(externalId).isEmpty();
    }

    @Test
    void sendRework_returnsEmptyOnConnectionFailure() {
        // Arrange — point at a port nobody listens on
        GpfReworkClient client = new GpfReworkClient(new RestTemplateBuilder(), "http://localhost:1/api/rework");

        // Act
        Optional<String> externalId = client.sendRework("490154203237518", "panne");

        // Assert
        assertThat(externalId).isEmpty();
    }

    @Test
    void sendRework_coercesNumericIdToString() {
        // Arrange — GPF may return numeric id; the client must String.valueOf-it.
        wireMock.stubFor(
            post(urlEqualTo("/api/rework"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{\"id\":12345}"))
        );
        GpfReworkClient client = newClient();

        // Act
        Optional<String> externalId = client.sendRework("490154203237518", "panne");

        // Assert
        assertThat(externalId).contains("12345");
    }
}
