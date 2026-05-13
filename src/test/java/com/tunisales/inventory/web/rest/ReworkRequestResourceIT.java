package com.tunisales.inventory.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tunisales.inventory.IntegrationTest;
import com.tunisales.inventory.client.GpfReworkClient;
import com.tunisales.inventory.client.PlatformNotificationClient;
import com.tunisales.inventory.domain.ReworkRequest;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.enumeration.ReworkStatus;
import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import com.tunisales.inventory.repository.ReworkRequestRepository;
import com.tunisales.inventory.repository.StockItemRepository;
import java.time.ZonedDateTime;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link com.tunisales.inventory.web.rest.ReworkRequestResource}
 * focused on the {@code /rework-requests/callback} webhook.
 *
 * <p>The webhook is intentionally NOT JWT-protected: it is signed via the
 * {@code X-Gpf-Signature} header carrying a shared secret. We therefore call it
 * unauthenticated.</p>
 */
@IntegrationTest
@AutoConfigureMockMvc
class ReworkRequestResourceIT {

    private static final String VALID_IMEI = "490154203237518";

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private EntityManager em;

    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private ReworkRequestRepository reworkRequestRepository;

    // Defaults to "change-me" per ReworkRequestResource @Value default; using that here
    // means tests run without overriding application-testdev.yml.
    @Value("${tunisales.gpf.rework.secret:change-me}")
    private String gpfSharedSecret;

    @MockBean
    private PlatformNotificationClient notificationClientBean;

    @MockBean
    private GpfReworkClient gpfReworkClientBean;

    private StockItem persistDefectiveItem() {
        StockItem item = StockItemResourceIT.createEntity(em);
        item.setImei(VALID_IMEI);
        item.setStatus(StockItemStatus.DEFECTIVE);
        return stockItemRepository.saveAndFlush(item);
    }

    private ReworkRequest persistPendingReworkRequest(StockItem item, String externalId) {
        ReworkRequest req = new ReworkRequest()
            .tenantId(item.getTenantId())
            .status(ReworkStatus.PENDING)
            .sentAt(ZonedDateTime.now())
            .stockItem(item);
        req.setExternalId(externalId);
        return reworkRequestRepository.saveAndFlush(req);
    }

    @Test
    @Transactional
    void callback_returnsUnauthorizedWhenSignatureMissing() throws Exception {
        // Arrange — no signature header.

        // Act + Assert
        restMockMvc
            .perform(
                post("/api/rework-requests/callback")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"externalId\":\"GPF-1\",\"status\":\"COMPLETED\"}")
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void callback_returnsUnauthorizedOnSignatureMismatch() throws Exception {
        // Arrange + Act + Assert
        restMockMvc
            .perform(
                post("/api/rework-requests/callback")
                    .header("X-Gpf-Signature", "wrong-secret-value")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"externalId\":\"GPF-1\",\"status\":\"COMPLETED\"}")
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void callback_returnsBadRequestOnUnknownStatusEnum() throws Exception {
        // Arrange — valid signature but bogus status.
        // Act + Assert
        restMockMvc
            .perform(
                post("/api/rework-requests/callback")
                    .header("X-Gpf-Signature", gpfSharedSecret)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"externalId\":\"GPF-XYZ\",\"status\":\"NOT_AN_ENUM\"}")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void callback_returnsNotFoundForUnknownExternalId() throws Exception {
        // Arrange — no rework request for that external id.
        // Act + Assert
        restMockMvc
            .perform(
                post("/api/rework-requests/callback")
                    .header("X-Gpf-Signature", gpfSharedSecret)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"externalId\":\"GPF-DOES-NOT-EXIST\",\"status\":\"COMPLETED\"}")
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void callback_appliesCompletedStatusAndPersistsPayload() throws Exception {
        // Arrange
        StockItem item = persistDefectiveItem();
        ReworkRequest req = persistPendingReworkRequest(item, "GPF-EXT-42");

        String body = "{\"externalId\":\"GPF-EXT-42\",\"status\":\"COMPLETED\",\"payloadJson\":\"{\\\"diagnosis\\\":\\\"ok\\\"}\"}";

        // Act
        restMockMvc
            .perform(
                post("/api/rework-requests/callback")
                    .header("X-Gpf-Signature", gpfSharedSecret)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ReworkStatus.COMPLETED.toString()))
            .andExpect(jsonPath("$.externalId").value("GPF-EXT-42"));

        // Assert — local record updated, completedAt set.
        ReworkRequest reloaded = reworkRequestRepository.findById(req.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(ReworkStatus.COMPLETED);
        assertThat(reloaded.getCompletedAt()).isNotNull();
        assertThat(reloaded.getPayloadJson()).contains("diagnosis");
    }

    @Test
    @Transactional
    void callback_appliesNonCompletedStatusWithoutTouchingCompletedAt() throws Exception {
        // Arrange
        StockItem item = persistDefectiveItem();
        ReworkRequest req = persistPendingReworkRequest(item, "GPF-EXT-99");

        // Act
        restMockMvc
            .perform(
                post("/api/rework-requests/callback")
                    .header("X-Gpf-Signature", gpfSharedSecret)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"externalId\":\"GPF-EXT-99\",\"status\":\"FAILED\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ReworkStatus.FAILED.toString()));

        // Assert — completedAt stays null for non-COMPLETED transitions.
        ReworkRequest reloaded = reworkRequestRepository.findById(req.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(ReworkStatus.FAILED);
        assertThat(reloaded.getCompletedAt()).isNull();
    }
}
