package com.tunisales.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.inventory.domain.enumeration.AuditResolution;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A StockAuditLine.
 */
@Entity
@Table(name = "stock_audit_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockAuditLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "found_physically", nullable = false)
    private Boolean foundPhysically;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolution")
    private AuditResolution resolution;

    @Size(max = 500)
    @Column(name = "resolution_note", length = 500)
    private String resolutionNote;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "stockMovements", "warehouse" }, allowSetters = true)
    private StockItem stockItem;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "auditLines", "warehouse" }, allowSetters = true)
    private StockAudit audit;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockAuditLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFoundPhysically() {
        return this.foundPhysically;
    }

    public StockAuditLine foundPhysically(Boolean foundPhysically) {
        this.setFoundPhysically(foundPhysically);
        return this;
    }

    public void setFoundPhysically(Boolean foundPhysically) {
        this.foundPhysically = foundPhysically;
    }

    public AuditResolution getResolution() {
        return this.resolution;
    }

    public StockAuditLine resolution(AuditResolution resolution) {
        this.setResolution(resolution);
        return this;
    }

    public void setResolution(AuditResolution resolution) {
        this.resolution = resolution;
    }

    public String getResolutionNote() {
        return this.resolutionNote;
    }

    public StockAuditLine resolutionNote(String resolutionNote) {
        this.setResolutionNote(resolutionNote);
        return this;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public StockAuditLine createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public StockItem getStockItem() {
        return this.stockItem;
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    public StockAuditLine stockItem(StockItem stockItem) {
        this.setStockItem(stockItem);
        return this;
    }

    public StockAudit getAudit() {
        return this.audit;
    }

    public void setAudit(StockAudit stockAudit) {
        this.audit = stockAudit;
    }

    public StockAuditLine audit(StockAudit stockAudit) {
        this.setAudit(stockAudit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockAuditLine)) {
            return false;
        }
        return id != null && id.equals(((StockAuditLine) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockAuditLine{" +
            "id=" + getId() +
            ", foundPhysically='" + getFoundPhysically() + "'" +
            ", resolution='" + getResolution() + "'" +
            ", resolutionNote='" + getResolutionNote() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
