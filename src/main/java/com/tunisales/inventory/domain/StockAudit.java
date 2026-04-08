package com.tunisales.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.inventory.domain.enumeration.AuditStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A StockAudit.
 */
@Entity
@Table(name = "stock_audit")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockAudit implements Serializable {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuditStatus status;

    @Min(value = 0)
    @Column(name = "theoretical_count")
    private Integer theoreticalCount;

    @Min(value = 0)
    @Column(name = "physical_count")
    private Integer physicalCount;

    @Min(value = 0)
    @Column(name = "discrepancy_count")
    private Integer discrepancyCount;

    @Size(max = 2000)
    @Column(name = "notes", length = 2000)
    private String notes;

    @NotNull
    @Size(max = 100)
    @Column(name = "auditor_login", length = 100, nullable = false)
    private String auditorLogin;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private ZonedDateTime startedAt;

    @Column(name = "closed_at")
    private ZonedDateTime closedAt;

    @OneToMany(mappedBy = "audit")
    @JsonIgnoreProperties(value = { "stockItem", "audit" }, allowSetters = true)
    private Set<StockAuditLine> auditLines = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "stockItems" }, allowSetters = true)
    private Warehouse warehouse;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockAudit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public StockAudit tenantId(Long tenantId) {
        this.setTenantId(tenantId);
        return this;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public AuditStatus getStatus() {
        return this.status;
    }

    public StockAudit status(AuditStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public Integer getTheoreticalCount() {
        return this.theoreticalCount;
    }

    public StockAudit theoreticalCount(Integer theoreticalCount) {
        this.setTheoreticalCount(theoreticalCount);
        return this;
    }

    public void setTheoreticalCount(Integer theoreticalCount) {
        this.theoreticalCount = theoreticalCount;
    }

    public Integer getPhysicalCount() {
        return this.physicalCount;
    }

    public StockAudit physicalCount(Integer physicalCount) {
        this.setPhysicalCount(physicalCount);
        return this;
    }

    public void setPhysicalCount(Integer physicalCount) {
        this.physicalCount = physicalCount;
    }

    public Integer getDiscrepancyCount() {
        return this.discrepancyCount;
    }

    public StockAudit discrepancyCount(Integer discrepancyCount) {
        this.setDiscrepancyCount(discrepancyCount);
        return this;
    }

    public void setDiscrepancyCount(Integer discrepancyCount) {
        this.discrepancyCount = discrepancyCount;
    }

    public String getNotes() {
        return this.notes;
    }

    public StockAudit notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAuditorLogin() {
        return this.auditorLogin;
    }

    public StockAudit auditorLogin(String auditorLogin) {
        this.setAuditorLogin(auditorLogin);
        return this;
    }

    public void setAuditorLogin(String auditorLogin) {
        this.auditorLogin = auditorLogin;
    }

    public ZonedDateTime getStartedAt() {
        return this.startedAt;
    }

    public StockAudit startedAt(ZonedDateTime startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(ZonedDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public ZonedDateTime getClosedAt() {
        return this.closedAt;
    }

    public StockAudit closedAt(ZonedDateTime closedAt) {
        this.setClosedAt(closedAt);
        return this;
    }

    public void setClosedAt(ZonedDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public Set<StockAuditLine> getAuditLines() {
        return this.auditLines;
    }

    public void setAuditLines(Set<StockAuditLine> stockAuditLines) {
        if (this.auditLines != null) {
            this.auditLines.forEach(i -> i.setAudit(null));
        }
        if (stockAuditLines != null) {
            stockAuditLines.forEach(i -> i.setAudit(this));
        }
        this.auditLines = stockAuditLines;
    }

    public StockAudit auditLines(Set<StockAuditLine> stockAuditLines) {
        this.setAuditLines(stockAuditLines);
        return this;
    }

    public StockAudit addAuditLines(StockAuditLine stockAuditLine) {
        this.auditLines.add(stockAuditLine);
        stockAuditLine.setAudit(this);
        return this;
    }

    public StockAudit removeAuditLines(StockAuditLine stockAuditLine) {
        this.auditLines.remove(stockAuditLine);
        stockAuditLine.setAudit(null);
        return this;
    }

    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public StockAudit warehouse(Warehouse warehouse) {
        this.setWarehouse(warehouse);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockAudit)) {
            return false;
        }
        return id != null && id.equals(((StockAudit) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockAudit{" +
            "id=" + getId() +
            ", tenantId=" + getTenantId() +
            ", status='" + getStatus() + "'" +
            ", theoreticalCount=" + getTheoreticalCount() +
            ", physicalCount=" + getPhysicalCount() +
            ", discrepancyCount=" + getDiscrepancyCount() +
            ", notes='" + getNotes() + "'" +
            ", auditorLogin='" + getAuditorLogin() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", closedAt='" + getClosedAt() + "'" +
            "}";
    }
}
