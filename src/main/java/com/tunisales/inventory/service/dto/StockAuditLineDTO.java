package com.tunisales.inventory.service.dto;

import com.tunisales.inventory.domain.enumeration.AuditResolution;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.tunisales.inventory.domain.StockAuditLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockAuditLineDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean foundPhysically;

    private AuditResolution resolution;

    @Size(max = 500)
    private String resolutionNote;

    @NotNull
    private ZonedDateTime createdAt;

    private StockItemDTO stockItem;

    private StockAuditDTO audit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFoundPhysically() {
        return foundPhysically;
    }

    public void setFoundPhysically(Boolean foundPhysically) {
        this.foundPhysically = foundPhysically;
    }

    public AuditResolution getResolution() {
        return resolution;
    }

    public void setResolution(AuditResolution resolution) {
        this.resolution = resolution;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public StockItemDTO getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItemDTO stockItem) {
        this.stockItem = stockItem;
    }

    public StockAuditDTO getAudit() {
        return audit;
    }

    public void setAudit(StockAuditDTO audit) {
        this.audit = audit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockAuditLineDTO)) {
            return false;
        }

        StockAuditLineDTO stockAuditLineDTO = (StockAuditLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockAuditLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockAuditLineDTO{" +
            "id=" + getId() +
            ", foundPhysically='" + getFoundPhysically() + "'" +
            ", resolution='" + getResolution() + "'" +
            ", resolutionNote='" + getResolutionNote() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", stockItem=" + getStockItem() +
            ", audit=" + getAudit() +
            "}";
    }
}
