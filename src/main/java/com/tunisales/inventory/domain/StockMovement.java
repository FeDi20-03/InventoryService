package com.tunisales.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.inventory.domain.enumeration.MovementType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A StockMovement.
 */
@Entity
@Table(name = "stock_movement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMovement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Size(max = 500)
    @Column(name = "reason", length = 500)
    private String reason;

    @Size(max = 100)
    @Column(name = "reference", length = 100)
    private String reference;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Size(max = 100)
    @Column(name = "performed_by_login", length = 100)
    private String performedByLogin;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @ManyToOne
    @JsonIgnoreProperties(value = { "stockItems" }, allowSetters = true)
    private Warehouse fromWarehouse;

    @ManyToOne
    @JsonIgnoreProperties(value = { "stockItems" }, allowSetters = true)
    private Warehouse toWarehouse;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "stockMovements", "warehouse" }, allowSetters = true)
    private StockItem stockItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockMovement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovementType getMovementType() {
        return this.movementType;
    }

    public StockMovement movementType(MovementType movementType) {
        this.setMovementType(movementType);
        return this;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public String getReason() {
        return this.reason;
    }

    public StockMovement reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReference() {
        return this.reference;
    }

    public StockMovement reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public StockMovement quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPerformedByLogin() {
        return this.performedByLogin;
    }

    public StockMovement performedByLogin(String performedByLogin) {
        this.setPerformedByLogin(performedByLogin);
        return this;
    }

    public void setPerformedByLogin(String performedByLogin) {
        this.performedByLogin = performedByLogin;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public StockMovement createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Warehouse getFromWarehouse() {
        return this.fromWarehouse;
    }

    public void setFromWarehouse(Warehouse warehouse) {
        this.fromWarehouse = warehouse;
    }

    public StockMovement fromWarehouse(Warehouse warehouse) {
        this.setFromWarehouse(warehouse);
        return this;
    }

    public Warehouse getToWarehouse() {
        return this.toWarehouse;
    }

    public void setToWarehouse(Warehouse warehouse) {
        this.toWarehouse = warehouse;
    }

    public StockMovement toWarehouse(Warehouse warehouse) {
        this.setToWarehouse(warehouse);
        return this;
    }

    public StockItem getStockItem() {
        return this.stockItem;
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    public StockMovement stockItem(StockItem stockItem) {
        this.setStockItem(stockItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockMovement)) {
            return false;
        }
        return id != null && id.equals(((StockMovement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMovement{" +
            "id=" + getId() +
            ", movementType='" + getMovementType() + "'" +
            ", reason='" + getReason() + "'" +
            ", reference='" + getReference() + "'" +
            ", quantity=" + getQuantity() +
            ", performedByLogin='" + getPerformedByLogin() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
