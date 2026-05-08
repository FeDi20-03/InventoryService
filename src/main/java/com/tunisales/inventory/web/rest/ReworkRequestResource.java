package com.tunisales.inventory.web.rest;

import com.tunisales.inventory.domain.enumeration.ReworkStatus;
import com.tunisales.inventory.security.AuthoritiesConstants;
import com.tunisales.inventory.service.ReworkRequestService;
import com.tunisales.inventory.service.dto.ReworkRequestDTO;
import com.tunisales.inventory.web.rest.errors.BadRequestAlertException;
import com.tunisales.inventory.web.rest.vm.ReworkCallbackVM;
import java.security.MessageDigest;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for {@link com.tunisales.inventory.domain.ReworkRequest}.
 *
 * <p>Two flavours of endpoint live here:
 * <ul>
 *   <li>The {@code GET} endpoint, secured by the standard role hierarchy.</li>
 *   <li>The webhook callback used by GPF to inform us of the outcome of a
 *       rework. It is secured by the shared secret
 *       {@code tunisales.gpf.rework.secret} (passed via the
 *       {@code X-Gpf-Signature} header) — this is intentionally not handled by
 *       the JWT filter chain because GPF cannot obtain a JWT.</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api")
public class ReworkRequestResource {

    private static final String ENTITY_NAME = "inventoryServiceReworkRequest";
    public static final String GPF_SIGNATURE_HEADER = "X-Gpf-Signature";

    private final Logger log = LoggerFactory.getLogger(ReworkRequestResource.class);

    private final ReworkRequestService reworkRequestService;

    @Value("${tunisales.gpf.rework.secret:change-me}")
    private String gpfSharedSecret;

    public ReworkRequestResource(ReworkRequestService reworkRequestService) {
        this.reworkRequestService = reworkRequestService;
    }

    /**
     * {@code GET /rework-requests/{id}} : retrieve a rework request.
     */
    @GetMapping("/rework-requests/{id}")
    @PreAuthorize(
        "hasAnyAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\", \"" +
        AuthoritiesConstants.ADMIN_COMMERCIAL +
        "\", \"" +
        AuthoritiesConstants.ADMIN_SYSTEME +
        "\", \"" +
        AuthoritiesConstants.MAGASINIER +
        "\")"
    )
    public ResponseEntity<ReworkRequestDTO> getReworkRequest(@PathVariable Long id) {
        log.debug("REST request to get ReworkRequest : {}", id);
        return reworkRequestService
            .findOne(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ReworkRequest " + id + " not found"));
    }

    /**
     * {@code POST /rework-requests/callback} : webhook called by GPF when a
     * rework request changes status. Authentication is performed via the shared
     * secret on the {@link #GPF_SIGNATURE_HEADER} header (constant-time
     * comparison).
     */
    @PostMapping("/rework-requests/callback")
    public ResponseEntity<ReworkRequestDTO> handleCallback(
        @RequestHeader(value = GPF_SIGNATURE_HEADER, required = false) String signature,
        @Valid @RequestBody ReworkCallbackVM body
    ) {
        log.debug("REST request to apply GPF rework callback for externalId={}", body.getExternalId());
        if (!isSignatureValid(signature)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid GPF signature");
        }
        ReworkStatus status;
        try {
            status = ReworkStatus.valueOf(body.getStatus());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestAlertException("Invalid status", ENTITY_NAME, "statusinvalid");
        }
        try {
            Optional<ReworkRequestDTO> updated = reworkRequestService.applyCallback(body.getExternalId(), status, body.getPayloadJson());
            return updated
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown externalId: " + body.getExternalId()));
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    private boolean isSignatureValid(String signature) {
        if (signature == null || gpfSharedSecret == null) {
            return false;
        }
        byte[] a = signature.getBytes();
        byte[] b = gpfSharedSecret.getBytes();
        if (a.length != b.length) {
            return false;
        }
        return MessageDigest.isEqual(a, b);
    }
}
