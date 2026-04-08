package com.tunisales.inventory.service.dto;

import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.tunisales.inventory.domain.StockItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockItemDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    private Long productId;

    @Size(max = 255)
    private String productName;

    @NotNull
    @Size(min = 15, max = 15)
    private String imei;

    @NotNull
    private StockItemStatus status;

    @NotNull
    private Boolean isDeleted;

    @NotNull
    private ZonedDateTime acquiredAt;

    private ZonedDateTime updatedAt;

    private WarehouseDTO warehouse;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public StockItemStatus getStatus() {
        return status;
    }

    public void setStatus(StockItemStatus status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ZonedDateTime getAcquiredAt() {
        return acquiredAt;
    }

    public void setAcquiredAt(ZonedDateTime acquiredAt) {
        this.acquiredAt = acquiredAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public WarehouseDTO getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseDTO warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockItemDTO)) {
            return false;
        }

        StockItemDTO stockItemDTO = (StockItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockItemDTO{" +
            "id=" + getId() +
            ", tenantId=" + getTenantId() +
            ", productId=" + getProductId() +
            ", productName='" + getProductName() + "'" +
            ", imei='" + getImei() + "'" +
            ", status='" + getStatus() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", acquiredAt='" + getAcquiredAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", warehouse=" + getWarehouse() +
            "}";
    }
}
