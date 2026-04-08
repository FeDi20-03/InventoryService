package com.tunisales.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.inventory.domain.enumeration.SwapStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Swap.
 */
@Entity
@Table(name = "swap")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Swap implements Serializable {

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
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Size(max = 255)
    @Column(name = "client_name", length = 255)
    private String clientName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SwapStatus status;

    @Size(max = 500)
    @Column(name = "reason", length = 500)
    private String reason;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "resolved_at")
    private ZonedDateTime resolvedAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "stockMovements", "warehouse" }, allowSetters = true)
    private StockItem outgoingItem;

    @ManyToOne
    @JsonIgnoreProperties(value = { "stockMovements", "warehouse" }, allowSetters = true)
    private StockItem incomingItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Swap id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public Swap tenantId(Long tenantId) {
        this.setTenantId(tenantId);
        return this;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public Swap clientId(Long clientId) {
        this.setClientId(clientId);
        return this;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return this.clientName;
    }

    public Swap clientName(String clientName) {
        this.setClientName(clientName);
        return this;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public SwapStatus getStatus() {
        return this.status;
    }

    public Swap status(SwapStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SwapStatus status) {
        this.status = status;
    }

    public String getReason() {
        return this.reason;
    }

    public Swap reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Swap createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getResolvedAt() {
        return this.resolvedAt;
    }

    public Swap resolvedAt(ZonedDateTime resolvedAt) {
        this.setResolvedAt(resolvedAt);
        return this;
    }

    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Swap updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StockItem getOutgoingItem() {
        return this.outgoingItem;
    }

    public void setOutgoingItem(StockItem stockItem) {
        this.outgoingItem = stockItem;
    }

    public Swap outgoingItem(StockItem stockItem) {
        this.setOutgoingItem(stockItem);
        return this;
    }

    public StockItem getIncomingItem() {
        return this.incomingItem;
    }

    public void setIncomingItem(StockItem stockItem) {
        this.incomingItem = stockItem;
    }

    public Swap incomingItem(StockItem stockItem) {
        this.setIncomingItem(stockItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Swap)) {
            return false;
        }
        return id != null && id.equals(((Swap) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Swap{" +
            "id=" + getId() +
            ", tenantId=" + getTenantId() +
            ", clientId=" + getClientId() +
            ", clientName='" + getClientName() + "'" +
            ", status='" + getStatus() + "'" +
            ", reason='" + getReason() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
