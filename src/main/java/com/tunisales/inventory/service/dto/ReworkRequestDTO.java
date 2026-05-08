package com.tunisales.inventory.service.dto;

import com.tunisales.inventory.domain.enumeration.ReworkStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO for the {@link com.tunisales.inventory.domain.ReworkRequest} entity.
 */
public class ReworkRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    private Long tenantId;

    @Size(max = 100)
    private String externalId;

    @NotNull
    private ReworkStatus status;

    @NotNull
    private ZonedDateTime sentAt;

    private ZonedDateTime completedAt;

    private String payloadJson;

    private StockItemDTO stockItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public ReworkStatus getStatus() {
        return status;
    }

    public void setStatus(ReworkStatus status) {
        this.status = status;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(ZonedDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public StockItemDTO getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItemDTO stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReworkRequestDTO)) return false;
        ReworkRequestDTO that = (ReworkRequestDTO) o;
        if (this.id == null) return false;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "ReworkRequestDTO{" +
            "id=" +
            id +
            ", tenantId=" +
            tenantId +
            ", externalId='" +
            externalId +
            '\'' +
            ", status=" +
            status +
            ", sentAt=" +
            sentAt +
            ", completedAt=" +
            completedAt +
            "}"
        );
    }
}
