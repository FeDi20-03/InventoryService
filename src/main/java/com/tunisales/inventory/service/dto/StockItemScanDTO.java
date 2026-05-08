package com.tunisales.inventory.service.dto;

import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Enriched read model returned by {@code POST /api/stock-items/scan}.
 *
 * <p>Carries the most useful information for a doucheur (barcode scanner)
 * workflow: where the device currently is and when it last moved.</p>
 */
public class StockItemScanDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tenantId;
    private Long productId;
    private String productName;
    private String imei;
    private StockItemStatus status;
    private Long warehouseId;
    private String warehouseName;
    private WarehouseType warehouseType;
    private ZonedDateTime lastMovementAt;

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

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public WarehouseType getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(WarehouseType warehouseType) {
        this.warehouseType = warehouseType;
    }

    public ZonedDateTime getLastMovementAt() {
        return lastMovementAt;
    }

    public void setLastMovementAt(ZonedDateTime lastMovementAt) {
        this.lastMovementAt = lastMovementAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockItemScanDTO)) {
            return false;
        }
        StockItemScanDTO that = (StockItemScanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "StockItemScanDTO{" +
            "id=" +
            id +
            ", imei='" +
            imei +
            "'" +
            ", status=" +
            status +
            ", warehouseId=" +
            warehouseId +
            ", warehouseName='" +
            warehouseName +
            "'" +
            ", warehouseType=" +
            warehouseType +
            ", lastMovementAt=" +
            lastMovementAt +
            "}"
        );
    }
}
