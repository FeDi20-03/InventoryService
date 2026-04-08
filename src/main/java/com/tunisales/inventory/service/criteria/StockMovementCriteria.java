package com.tunisales.inventory.service.criteria;

import com.tunisales.inventory.domain.enumeration.MovementType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.inventory.domain.StockMovement} entity. This class is used
 * in {@link com.tunisales.inventory.web.rest.StockMovementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stock-movements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMovementCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MovementType
     */
    public static class MovementTypeFilter extends Filter<MovementType> {

        public MovementTypeFilter() {}

        public MovementTypeFilter(MovementTypeFilter filter) {
            super(filter);
        }

        @Override
        public MovementTypeFilter copy() {
            return new MovementTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private MovementTypeFilter movementType;

    private StringFilter reason;

    private StringFilter reference;

    private IntegerFilter quantity;

    private StringFilter performedByLogin;

    private ZonedDateTimeFilter createdAt;

    private LongFilter fromWarehouseId;

    private LongFilter toWarehouseId;

    private LongFilter stockItemId;

    private Boolean distinct;

    public StockMovementCriteria() {}

    public StockMovementCriteria(StockMovementCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.movementType = other.movementType == null ? null : other.movementType.copy();
        this.reason = other.reason == null ? null : other.reason.copy();
        this.reference = other.reference == null ? null : other.reference.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.performedByLogin = other.performedByLogin == null ? null : other.performedByLogin.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.fromWarehouseId = other.fromWarehouseId == null ? null : other.fromWarehouseId.copy();
        this.toWarehouseId = other.toWarehouseId == null ? null : other.toWarehouseId.copy();
        this.stockItemId = other.stockItemId == null ? null : other.stockItemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StockMovementCriteria copy() {
        return new StockMovementCriteria(this);
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

    public MovementTypeFilter getMovementType() {
        return movementType;
    }

    public MovementTypeFilter movementType() {
        if (movementType == null) {
            movementType = new MovementTypeFilter();
        }
        return movementType;
    }

    public void setMovementType(MovementTypeFilter movementType) {
        this.movementType = movementType;
    }

    public StringFilter getReason() {
        return reason;
    }

    public StringFilter reason() {
        if (reason == null) {
            reason = new StringFilter();
        }
        return reason;
    }

    public void setReason(StringFilter reason) {
        this.reason = reason;
    }

    public StringFilter getReference() {
        return reference;
    }

    public StringFilter reference() {
        if (reference == null) {
            reference = new StringFilter();
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            quantity = new IntegerFilter();
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public StringFilter getPerformedByLogin() {
        return performedByLogin;
    }

    public StringFilter performedByLogin() {
        if (performedByLogin == null) {
            performedByLogin = new StringFilter();
        }
        return performedByLogin;
    }

    public void setPerformedByLogin(StringFilter performedByLogin) {
        this.performedByLogin = performedByLogin;
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

    public LongFilter getFromWarehouseId() {
        return fromWarehouseId;
    }

    public LongFilter fromWarehouseId() {
        if (fromWarehouseId == null) {
            fromWarehouseId = new LongFilter();
        }
        return fromWarehouseId;
    }

    public void setFromWarehouseId(LongFilter fromWarehouseId) {
        this.fromWarehouseId = fromWarehouseId;
    }

    public LongFilter getToWarehouseId() {
        return toWarehouseId;
    }

    public LongFilter toWarehouseId() {
        if (toWarehouseId == null) {
            toWarehouseId = new LongFilter();
        }
        return toWarehouseId;
    }

    public void setToWarehouseId(LongFilter toWarehouseId) {
        this.toWarehouseId = toWarehouseId;
    }

    public LongFilter getStockItemId() {
        return stockItemId;
    }

    public LongFilter stockItemId() {
        if (stockItemId == null) {
            stockItemId = new LongFilter();
        }
        return stockItemId;
    }

    public void setStockItemId(LongFilter stockItemId) {
        this.stockItemId = stockItemId;
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
        final StockMovementCriteria that = (StockMovementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(movementType, that.movementType) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(performedByLogin, that.performedByLogin) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(fromWarehouseId, that.fromWarehouseId) &&
            Objects.equals(toWarehouseId, that.toWarehouseId) &&
            Objects.equals(stockItemId, that.stockItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            movementType,
            reason,
            reference,
            quantity,
            performedByLogin,
            createdAt,
            fromWarehouseId,
            toWarehouseId,
            stockItemId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMovementCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (movementType != null ? "movementType=" + movementType + ", " : "") +
            (reason != null ? "reason=" + reason + ", " : "") +
            (reference != null ? "reference=" + reference + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (performedByLogin != null ? "performedByLogin=" + performedByLogin + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (fromWarehouseId != null ? "fromWarehouseId=" + fromWarehouseId + ", " : "") +
            (toWarehouseId != null ? "toWarehouseId=" + toWarehouseId + ", " : "") +
            (stockItemId != null ? "stockItemId=" + stockItemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
