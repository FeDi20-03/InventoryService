package com.tunisales.inventory.client;

import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Outbound HTTP client for the GPF (Gestionnaire de Pannes Fournisseur) rework
 * endpoint.
 *
 * <p>Sends {@code {"imei", "defectReason"}} POST bodies and returns the
 * external request id from the response when present (so it can be persisted on
 * the local {@link com.tunisales.inventory.domain.ReworkRequest} for callback
 * correlation).</p>
 */
@Component
public class GpfReworkClient {

    private static final Logger log = LoggerFactory.getLogger(GpfReworkClient.class);

    private final RestTemplate restTemplate;
    private final String reworkUrl;

    public GpfReworkClient(
        RestTemplateBuilder builder,
        @Value("${tunisales.gpf.rework.url:http://gpf.example/api/rework}") String reworkUrl
    ) {
        this.restTemplate = builder.build();
        this.reworkUrl = reworkUrl;
    }

    /**
     * Send a rework request to GPF.
     *
     * @return the external GPF id if the response contained an {@code id} field,
     *         empty otherwise (or on error). Errors are logged at WARN — the
     *         caller's transaction is never aborted by transport failures.
     */
    public Optional<String> sendRework(String imei, String defectReason) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(
                Map.of("imei", imei, "defectReason", defectReason == null ? "" : defectReason),
                headers
            );
            ResponseEntity<Map> response = restTemplate.postForEntity(reworkUrl, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object id = response.getBody().get("id");
                return id == null ? Optional.empty() : Optional.of(String.valueOf(id));
            }
            log.warn("GPF rework call returned non-2xx status {}", response.getStatusCode());
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("Failed to call GPF rework endpoint for IMEI {}: {}", imei, ex.getMessage());
            return Optional.empty();
        }
    }
}
