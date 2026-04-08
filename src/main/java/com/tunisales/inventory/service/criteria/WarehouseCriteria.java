package com.tunisales.inventory.service.criteria;

import com.tunisales.inventory.domain.enumeration.WarehouseType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.inventory.domain.Warehouse} entity. This class is used
 * in {@link com.tunisales.inventory.web.rest.WarehouseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /warehouses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseCriteria implements Serializable, Criteria {

    /**
     * Class for filtering WarehouseType
     */
    public static class WarehouseTypeFilter extends Filter<WarehouseType> {

        public WarehouseTypeFilter() {}

        public WarehouseTypeFilter(WarehouseTypeFilter filter) {
            super(filter);
        }

        @Override
        public WarehouseTypeFilter copy() {
            return new WarehouseTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tenantId;

    private StringFilter name;

    private WarehouseTypeFilter type;

    private StringFilter address;

    private StringFilter city;

    private IntegerFilter minThreshold;

    private BooleanFilter isActive;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter updatedAt;

    private LongFilter stockItemsId;

    private Boolean distinct;

    public WarehouseCriteria() {}

    public WarehouseCriteria(WarehouseCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tenantId = other.tenantId == null ? null : other.tenantId.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.city = other.city == null ? null : other.city.copy();
        this.minThreshold = other.minThreshold == null ? null : other.minThreshold.copy();
        this.isActive = other.isActive == null ? null : other.isActive.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.stockItemsId = other.stockItemsId == null ? null : other.stockItemsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WarehouseCriteria copy() {
        return new WarehouseCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public WarehouseTypeFilter getType() {
        return type;
    }

    public WarehouseTypeFilter type() {
        if (type == null) {
            type = new WarehouseTypeFilter();
        }
        return type;
    }

    public void setType(WarehouseTypeFilter type) {
        this.type = type;
    }

    public StringFilter getAddress() {
        return address;
    }

    public StringFilter address() {
        if (address == null) {
            address = new StringFilter();
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getCity() {
        return city;
    }

    public StringFilter city() {
        if (city == null) {
            city = new StringFilter();
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public IntegerFilter getMinThreshold() {
        return minThreshold;
    }

    public IntegerFilter minThreshold() {
        if (minThreshold == null) {
            minThreshold = new IntegerFilter();
        }
        return minThreshold;
    }

    public void setMinThreshold(IntegerFilter minThreshold) {
        this.minThreshold = minThreshold;
    }

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            isActive = new BooleanFilter();
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
    }

    public ZonedDateTimeFilter getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTimeFilter createdAt() {
        if (createdAt == null) {
            createdAt = new ZonedDateTimeFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
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

    public LongFilter getStockItemsId() {
        return stockItemsId;
    }

    public LongFilter stockItemsId() {
        if (stockItemsId == null) {
            stockItemsId = new LongFilter();
        }
        return stockItemsId;
    }

    public void setStockItemsId(LongFilter stockItemsId) {
        this.stockItemsId = stockItemsId;
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
        final WarehouseCriteria that = (WarehouseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(address, that.address) &&
            Objects.equals(city, that.city) &&
            Objects.equals(minThreshold, that.minThreshold) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(stockItemsId, that.stockItemsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, name, type, address, city, minThreshold, isActive, createdAt, updatedAt, stockItemsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tenantId != null ? "tenantId=" + tenantId + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (address != null ? "address=" + address + ", " : "") +
            (city != null ? "city=" + city + ", " : "") +
            (minThreshold != null ? "minThreshold=" + minThreshold + ", " : "") +
            (isActive != null ? "isActive=" + isActive + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (stockItemsId != null ? "stockItemsId=" + stockItemsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
