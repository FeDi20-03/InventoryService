package com.tunisales.inventory.service.dto;

import com.tunisales.inventory.domain.enumeration.MovementType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.tunisales.inventory.domain.StockMovement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMovementDTO implements Serializable {

    private Long id;

    @NotNull
    private MovementType movementType;

    @Size(max = 500)
    private String reason;

    @Size(max = 100)
    private String reference;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    @Size(max = 100)
    private String performedByLogin;

    @NotNull
    private ZonedDateTime createdAt;

    private WarehouseDTO fromWarehouse;

    private WarehouseDTO toWarehouse;

    private StockItemDTO stockItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPerformedByLogin() {
        return performedByLogin;
    }

    public void setPerformedByLogin(String performedByLogin) {
        this.performedByLogin = performedByLogin;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public WarehouseDTO getFromWarehouse() {
        return fromWarehouse;
    }

    public void setFromWarehouse(WarehouseDTO fromWarehouse) {
        this.fromWarehouse = fromWarehouse;
    }

    public WarehouseDTO getToWarehouse() {
        return toWarehouse;
    }

    public void setToWarehouse(WarehouseDTO toWarehouse) {
        this.toWarehouse = toWarehouse;
    }

    public StockItemDTO getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItemDTO stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockMovementDTO)) {
            return false;
        }

        StockMovementDTO stockMovementDTO = (StockMovementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockMovementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMovementDTO{" +
            "id=" + getId() +
            ", movementType='" + getMovementType() + "'" +
            ", reason='" + getReason() + "'" +
            ", reference='" + getReference() + "'" +
            ", quantity=" + getQuantity() +
            ", performedByLogin='" + getPerformedByLogin() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", fromWarehouse=" + getFromWarehouse() +
            ", toWarehouse=" + getToWarehouse() +
            ", stockItem=" + getStockItem() +
            "}";
    }
}
