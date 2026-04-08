package com.tunisales.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Warehouse.
 */
@Entity
@Table(name = "warehouse")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Warehouse implements Serializable {

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
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private WarehouseType type;

    @Size(max = 500)
    @Column(name = "address", length = 500)
    private String address;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Min(value = 0)
    @Column(name = "min_threshold")
    private Integer minThreshold;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @OneToMany(mappedBy = "warehouse")
    @JsonIgnoreProperties(value = { "stockMovements", "warehouse" }, allowSetters = true)
    private Set<StockItem> stockItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Warehouse id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public Warehouse tenantId(Long tenantId) {
        this.setTenantId(tenantId);
        return this;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return this.name;
    }

    public Warehouse name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WarehouseType getType() {
        return this.type;
    }

    public Warehouse type(WarehouseType type) {
        this.setType(type);
        return this;
    }

    public void setType(WarehouseType type) {
        this.type = type;
    }

    public String getAddress() {
        return this.address;
    }

    public Warehouse address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public Warehouse city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getMinThreshold() {
        return this.minThreshold;
    }

    public Warehouse minThreshold(Integer minThreshold) {
        this.setMinThreshold(minThreshold);
        return this;
    }

    public void setMinThreshold(Integer minThreshold) {
        this.minThreshold = minThreshold;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Warehouse isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Warehouse createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Warehouse updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<StockItem> getStockItems() {
        return this.stockItems;
    }

    public void setStockItems(Set<StockItem> stockItems) {
        if (this.stockItems != null) {
            this.stockItems.forEach(i -> i.setWarehouse(null));
        }
        if (stockItems != null) {
            stockItems.forEach(i -> i.setWarehouse(this));
        }
        this.stockItems = stockItems;
    }

    public Warehouse stockItems(Set<StockItem> stockItems) {
        this.setStockItems(stockItems);
        return this;
    }

    public Warehouse addStockItems(StockItem stockItem) {
        this.stockItems.add(stockItem);
        stockItem.setWarehouse(this);
        return this;
    }

    public Warehouse removeStockItems(StockItem stockItem) {
        this.stockItems.remove(stockItem);
        stockItem.setWarehouse(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Warehouse)) {
            return false;
        }
        return id != null && id.equals(((Warehouse) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Warehouse{" +
            "id=" + getId() +
            ", tenantId=" + getTenantId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", minThreshold=" + getMinThreshold() +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
