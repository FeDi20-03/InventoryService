package com.tunisales.inventory.web.rest.vm;

import java.io.Serializable;
import javax.validation.constraints.Size;

/**
 * Request body for {@code POST /api/stock-items/{id}/send-to-rework}.
 * The reason is forwarded to GPF as the {@code defectReason} field.
 */
public class SendToReworkVM implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 500)
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
