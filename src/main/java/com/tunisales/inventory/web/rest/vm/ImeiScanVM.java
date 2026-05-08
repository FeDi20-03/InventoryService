package com.tunisales.inventory.web.rest.vm;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * View model for the {@code POST /api/stock-items/scan} request body.
 */
public class ImeiScanVM {

    @NotBlank
    @Size(min = 15, max = 15)
    private String imei;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
