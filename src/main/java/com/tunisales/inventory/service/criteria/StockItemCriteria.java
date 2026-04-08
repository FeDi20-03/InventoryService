package com.tunisales.inventory.service.criteria;

import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.inventory.domain.StockItem} entity. This class is used
 * in {@link com.tunisales.inventory.web.rest.StockItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stock-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockItemCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StockItemStatus
     */
    public static class StockItemStatusFilter extends Filter<StockItemStatus> {

        public StockItemStatusFilter() {}

        public StockItemStatusFilter(StockItemStatusFilter filter) {
            super(filter);
        }

        @Override
        public StockItemStatusFilter copy() {
            return new StockItemStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tenantId;

    private LongFilter productId;

    private StringFilter productName;

    private StringFilter imei;

    private StockItemStatusFilter status;

    private BooleanFilter isDeleted;

    private ZonedDateTimeFilter acquiredAt;

    private ZonedDateTimeFilter updatedAt;

    private LongFilter stockMovementsId;

    private LongFilter warehouseId;

    private Boolean distinct;

    public StockItemCriteria() {}

    public StockItemCriteria(StockItemCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tenantId = other.tenantId == null ? null : other.tenantId.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.productName = other.productName == null ? null : other.productName.copy();
        this.imei = other.imei == null ? null : other.imei.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.isDeleted = other.isDeleted == null ? null : other.isDeleted.copy();
        this.acquiredAt = other.acquiredAt == null ? null : other.acquiredAt.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.stockMovementsId = other.stockMovementsId == null ? null : other.stockMovementsId.copy();
        this.warehouseId = other.warehouseId == null ? null : other.warehouseId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StockItemCriteria copy() {
        return new StockItemCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getTenantId() {
        return tenantId;
    }

    public LongFilter tenantId() {
        if (tenantId == null) {
            tenantId = new LongFilter();
        }
        return tenantId;
    }

    public void setTenantId(LongFilter tenantId) {
        this.tenantId = tenantId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public LongFilter productId() {
        if (productId == null) {
            productId = new LongFilter();
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public StringFilter getProductName() {
        return productName;
    }

    public StringFilter productName() {
        if (productName == null) {
            productName = new StringFilter();
        }
        return productName;
    }

    public void setProductName(StringFilter productName) {
        this.productName = productName;
    }

    public StringFilter getImei() {
        return imei;
    }

    public StringFilter imei() {
        if (imei == null) {
            imei = new StringFilter();
        }
        return imei;
    }

    public void setImei(StringFilter imei) {
        this.imei = imei;
    }

    public StockItemStatusFilter getStatus() {
        return status;
    }

    public StockItemStatusFilter status() {
        if (status == null) {
            status = new StockItemStatusFilter();
        }
        return status;
    }

    public void setStatus(StockItemStatusFilter status) {
        this.status = status;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            isDeleted = new BooleanFilter();
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ZonedDateTimeFilter getAcquiredAt() {
        return acquiredAt;
    }

    public ZonedDateTimeFilter acquiredAt() {
        if (acquiredAt == null) {
            acquiredAt = new ZonedDateTimeFilter();
        }
        return acquiredAt;
    }

    public void setAcquiredAt(ZonedDateTimeFilter acquiredAt) {
        this.acquiredAt = acquiredAt;
    }

    public ZonedDateTimeFilter getUpdatedAt() {
        return updatedAt;
    }

    public ZonedDateTimeFilter updatedAt() {
        if (updatedAt == null) {
            updatedAt = new ZonedDateTimeFilter();
        }
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTimeFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getStockMovementsId() {
        return stockMovementsId;
    }

    public LongFilter stockMovementsId() {
        if (stockMovementsId == null) {
            stockMovementsId = new LongFilter();
        }
        return stockMovementsId;
    }

    public void setStockMovementsId(LongFilter stockMovementsId) {
        this.stockMovementsId = stockMovementsId;
    }

    public LongFilter getWarehouseId() {
        return warehouseId;
    }

    public LongFilter warehouseId() {
        if (warehouseId == null) {
            warehouseId = new LongFilter();
        }
        return warehouseId;
    }

    public void setWarehouseId(LongFilter warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StockItemCriteria that = (StockItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(productName, that.productName) &&
            Objects.equals(imei, that.imei) &&
            Objects.equals(status, that.status) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(acquiredAt, that.acquiredAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(stockMovementsId, that.stockMovementsId) &&
            Objects.equals(warehouseId, that.warehouseId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            tenantId,
            productId,
            productName,
            imei,
            status,
            isDeleted,
            acquiredAt,
            updatedAt,
            stockMovementsId,
            warehouseId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockItemCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tenantId != null ? "tenantId=" + tenantId + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            (productName != null ? "productName=" + productName + ", " : "") +
            (imei != null ? "imei=" + imei + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (isDeleted != null ? "isDeleted=" + isDeleted + ", " : "") +
            (acquiredAt != null ? "acquiredAt=" + acquiredAt + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (stockMovementsId != null ? "stockMovementsId=" + stockMovementsId + ", " : "") +
            (warehouseId != null ? "warehouseId=" + warehouseId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
