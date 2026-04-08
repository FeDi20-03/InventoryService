package com.tunisales.inventory.service.dto;

import com.tunisales.inventory.domain.enumeration.SwapStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.tunisales.inventory.domain.Swap} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SwapDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    private Long clientId;

    @Size(max = 255)
    private String clientName;

    @NotNull
    private SwapStatus status;

    @Size(max = 500)
    private String reason;

    @NotNull
    private ZonedDateTime createdAt;

    private ZonedDateTime resolvedAt;

    private ZonedDateTime updatedAt;

    private StockItemDTO outgoingItem;

    private StockItemDTO incomingItem;

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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public SwapStatus getStatus() {
        return status;
    }

    public void setStatus(SwapStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StockItemDTO getOutgoingItem() {
        return outgoingItem;
    }

    public void setOutgoingItem(StockItemDTO outgoingItem) {
        this.outgoingItem = outgoingItem;
    }

    public StockItemDTO getIncomingItem() {
        return incomingItem;
    }

    public void setIncomingItem(StockItemDTO incomingItem) {
        this.incomingItem = incomingItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SwapDTO)) {
            return false;
        }

        SwapDTO swapDTO = (SwapDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, swapDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SwapDTO{" +
            "id=" + getId() +
            ", tenantId=" + getTenantId() +
            ", clientId=" + getClientId() +
            ", clientName='" + getClientName() + "'" +
            ", status='" + getStatus() + "'" +
            ", reason='" + getReason() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", outgoingItem=" + getOutgoingItem() +
            ", incomingItem=" + getIncomingItem() +
            "}";
    }
}
