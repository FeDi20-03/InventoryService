package com.tunisales.inventory.client;

import com.tunisales.inventory.service.dto.NotificationPayloadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Thin HTTP client used by the inventory service to publish business-level
 * notifications to the PlatformService.
 *
 * <p>The publish endpoint is fire-and-forget from inventory's perspective:
 * notification delivery must <strong>never</strong> roll back an inventory
 * transaction. Failures are logged at WARN.</p>
 *
 * <p>The HTTP call is also annotated with {@link Async} so it is dispatched
 * outside the calling DB transaction.</p>
 */
@Component
public class PlatformNotificationClient {

    private static final Logger log = LoggerFactory.getLogger(PlatformNotificationClient.class);

    private final RestTemplate restTemplate;
    private final String platformBaseUrl;

    public PlatformNotificationClient(
        RestTemplateBuilder restTemplateBuilder,
        @Value("${tunisales.platform.url:http://platformservice}") String platformBaseUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.platformBaseUrl = platformBaseUrl;
    }

    /**
     * Send a notification asynchronously. Errors are swallowed and logged.
     */
    @Async
    public void publish(NotificationPayloadDTO payload) {
        publishInternal(payload);
    }

    /**
     * Synchronous variant used in tests and when called from non-Spring contexts.
     */
    public void publishInternal(NotificationPayloadDTO payload) {
        if (payload == null) {
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<NotificationPayloadDTO> request = new HttpEntity<>(payload, headers);
            String url = trimTrailingSlash(platformBaseUrl) + "/api/notifications/publish";
            restTemplate.postForEntity(url, request, Void.class);
        } catch (Exception ex) {
            // Notification dispatch must never break the originating transaction.
            log.warn("Failed to publish notification of type '{}' to PlatformService: {}", payload.getType(), ex.getMessage());
        }
    }

    private static String trimTrailingSlash(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
