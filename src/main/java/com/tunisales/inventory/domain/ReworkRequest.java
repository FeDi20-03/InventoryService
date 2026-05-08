package com.tunisales.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.inventory.domain.enumeration.ReworkStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A rework request sent to the GPF (Gestionnaire de Pannes Fournisseur) system.
 *
 * <p>Each request tracks one defective {@link StockItem}. The
 * {@code externalId} field stores the identifier returned by GPF (when present)
 * so subsequent callbacks can be correlated.</p>
 */
@Entity
@Table(name = "rework_request")
public class ReworkRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Size(max = 100)
    @Column(name = "external_id", length = 100)
    private String externalId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReworkStatus status;

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private ZonedDateTime sentAt;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Lob
    @Column(name = "payload_json")
    private String payloadJson;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "stockMovements", "warehouse" }, allowSetters = true)
    private StockItem stockItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ReworkRequest tenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getExternalId() {
        return externalId;
    }

    public ReworkRequest externalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public ReworkStatus getStatus() {
        return status;
    }

    public ReworkRequest status(ReworkStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ReworkStatus status) {
        this.status = status;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public ReworkRequest sentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getCompletedAt() {
        return completedAt;
    }

    public ReworkRequest completedAt(ZonedDateTime completedAt) {
        this.completedAt = completedAt;
        return this;
    }

    public void setCompletedAt(ZonedDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public ReworkRequest payloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
        return this;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    public ReworkRequest stockItem(StockItem stockItem) {
        this.stockItem = stockItem;
        return this;
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReworkRequest)) return false;
        return id != null && id.equals(((ReworkRequest) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "ReworkRequest{id=" +
            id +
            ", tenantId=" +
            tenantId +
            ", externalId='" +
            externalId +
            "', status=" +
            status +
            ", sentAt=" +
            sentAt +
            ", completedAt=" +
            completedAt +
            "}"
        );
    }
}
