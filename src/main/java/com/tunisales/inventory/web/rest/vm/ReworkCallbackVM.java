package com.tunisales.inventory.web.rest.vm;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Body of the GPF rework callback webhook.
 *
 * <p>The signature is carried in the {@code X-Gpf-Signature} HTTP header rather
 * than this body, so it is not declared here.</p>
 */
public class ReworkCallbackVM implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Size(max = 100)
    private String externalId;

    @NotBlank
    @Size(max = 30)
    private String status;

    private String payloadJson;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }
}
