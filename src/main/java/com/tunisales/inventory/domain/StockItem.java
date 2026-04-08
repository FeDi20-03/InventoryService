package com.tunisales.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A StockItem.
 */
@Entity
@Table(name = "stock_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Size(max = 255)
    @Column(name = "product_name", length = 255)
    private String productName;

    @NotNull
    @Size(min = 15, max = 15)
    @Column(name = "imei", length = 15, nullable = false, unique = true)
    private String imei;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StockItemStatus status;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @NotNull
    @Column(name = "acquired_at", nullable = false)
    private ZonedDateTime acquiredAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @OneToMany(mappedBy = "stockItem")
    @JsonIgnoreProperties(value = { "fromWarehouse", "toWarehouse", "stockItem" }, allowSetters = true)
    private Set<StockMovement> stockMovements = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "stockItems" }, allowSetters = true)
    private Warehouse warehouse;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public StockItem tenantId(Long tenantId) {
        this.setTenantId(tenantId);
        return this;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProductId() {
        return this.productId;
    }

    public StockItem productId(Long productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public StockItem productName(String productName) {
        this.setProductName(productName);
        return this;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImei() {
        return this.imei;
    }

    public StockItem imei(String imei) {
        this.setImei(imei);
        return this;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public StockItemStatus getStatus() {
        return this.status;
    }

    public StockItem status(StockItemStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(StockItemStatus status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public StockItem isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ZonedDateTime getAcquiredAt() {
        return this.acquiredAt;
    }

    public StockItem acquiredAt(ZonedDateTime acquiredAt) {
        this.setAcquiredAt(acquiredAt);
        return this;
    }

    public void setAcquiredAt(ZonedDateTime acquiredAt) {
        this.acquiredAt = acquiredAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public StockItem updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<StockMovement> getStockMovements() {
        return this.stockMovements;
    }

    public void setStockMovements(Set<StockMovement> stockMovements) {
        if (this.stockMovements != null) {
            this.stockMovements.forEach(i -> i.setStockItem(null));
        }
        if (stockMovements != null) {
            stockMovements.forEach(i -> i.setStockItem(this));
        }
        this.stockMovements = stockMovements;
    }

    public StockItem stockMovements(Set<StockMovement> stockMovements) {
        this.setStockMovements(stockMovements);
        return this;
    }

    public StockItem addStockMovements(StockMovement stockMovement) {
        this.stockMovements.add(stockMovement);
        stockMovement.setStockItem(this);
        return this;
    }

    public StockItem removeStockMovements(StockMovement stockMovement) {
        this.stockMovements.remove(stockMovement);
        stockMovement.setStockItem(null);
        return this;
    }

    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public StockItem warehouse(Warehouse warehouse) {
        this.setWarehouse(warehouse);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockItem)) {
            return false;
        }
        return id != null && id.equals(((StockItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockItem{" +
            "id=" + getId() +
            ", tenantId=" + getTenantId() +
            ", productId=" + getProductId() +
            ", productName='" + getProductName() + "'" +
            ", imei='" + getImei() + "'" +
            ", status='" + getStatus() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", acquiredAt='" + getAcquiredAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
