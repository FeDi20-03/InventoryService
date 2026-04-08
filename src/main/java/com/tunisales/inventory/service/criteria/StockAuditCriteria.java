package com.tunisales.inventory.service.criteria;

import com.tunisales.inventory.domain.enumeration.AuditStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.inventory.domain.StockAudit} entity. This class is used
 * in {@link com.tunisales.inventory.web.rest.StockAuditResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stock-audits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockAuditCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AuditStatus
     */
    public static class AuditStatusFilter extends Filter<AuditStatus> {

        public AuditStatusFilter() {}

        public AuditStatusFilter(AuditStatusFilter filter) {
            super(filter);
        }

        @Override
        public AuditStatusFilter copy() {
            return new AuditStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tenantId;

    private AuditStatusFilter status;

    private IntegerFilter theoreticalCount;

    private IntegerFilter physicalCount;

    private IntegerFilter discrepancyCount;

    private StringFilter notes;

    private StringFilter auditorLogin;

    private ZonedDateTimeFilter startedAt;

    private ZonedDateTimeFilter closedAt;

    private LongFilter auditLinesId;

    private LongFilter warehouseId;

    private Boolean distinct;

    public StockAuditCriteria() {}

    public StockAuditCriteria(StockAuditCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tenantId = other.tenantId == null ? null : other.tenantId.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.theoreticalCount = other.theoreticalCount == null ? null : other.theoreticalCount.copy();
        this.physicalCount = other.physicalCount == null ? null : other.physicalCount.copy();
        this.discrepancyCount = other.discrepancyCount == null ? null : other.discrepancyCount.copy();
        this.notes = other.notes == null ? null : other.notes.copy();
        this.auditorLogin = other.auditorLogin == null ? null : other.auditorLogin.copy();
        this.startedAt = other.startedAt == null ? null : other.startedAt.copy();
        this.closedAt = other.closedAt == null ? null : other.closedAt.copy();
        this.auditLinesId = other.auditLinesId == null ? null : other.auditLinesId.copy();
        this.warehouseId = other.warehouseId == null ? null : other.warehouseId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StockAuditCriteria copy() {
        return new StockAuditCriteria(this);
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

    public AuditStatusFilter getStatus() {
        return status;
    }

    public AuditStatusFilter status() {
        if (status == null) {
            status = new AuditStatusFilter();
        }
        return status;
    }

    public void setStatus(AuditStatusFilter status) {
        this.status = status;
    }

    public IntegerFilter getTheoreticalCount() {
        return theoreticalCount;
    }

    public IntegerFilter theoreticalCount() {
        if (theoreticalCount == null) {
            theoreticalCount = new IntegerFilter();
        }
        return theoreticalCount;
    }

    public void setTheoreticalCount(IntegerFilter theoreticalCount) {
        this.theoreticalCount = theoreticalCount;
    }

    public IntegerFilter getPhysicalCount() {
        return physicalCount;
    }

    public IntegerFilter physicalCount() {
        if (physicalCount == null) {
            physicalCount = new IntegerFilter();
        }
        return physicalCount;
    }

    public void setPhysicalCount(IntegerFilter physicalCount) {
        this.physicalCount = physicalCount;
    }

    public IntegerFilter getDiscrepancyCount() {
        return discrepancyCount;
    }

    public IntegerFilter discrepancyCount() {
        if (discrepancyCount == null) {
            discrepancyCount = new IntegerFilter();
        }
        return discrepancyCount;
    }

    public void setDiscrepancyCount(IntegerFilter discrepancyCount) {
        this.discrepancyCount = discrepancyCount;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public StringFilter notes() {
        if (notes == null) {
            notes = new StringFilter();
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public StringFilter getAuditorLogin() {
        return auditorLogin;
    }

    public StringFilter auditorLogin() {
        if (auditorLogin == null) {
            auditorLogin = new StringFilter();
        }
        return auditorLogin;
    }

    public void setAuditorLogin(StringFilter auditorLogin) {
        this.auditorLogin = auditorLogin;
    }

    public ZonedDateTimeFilter getStartedAt() {
        return startedAt;
    }

    public ZonedDateTimeFilter startedAt() {
        if (startedAt == null) {
            startedAt = new ZonedDateTimeFilter();
        }
        return startedAt;
    }

    public void setStartedAt(ZonedDateTimeFilter startedAt) {
        this.startedAt = startedAt;
    }

    public ZonedDateTimeFilter getClosedAt() {
        return closedAt;
    }

    public ZonedDateTimeFilter closedAt() {
        if (closedAt == null) {
            closedAt = new ZonedDateTimeFilter();
        }
        return closedAt;
    }

    public void setClosedAt(ZonedDateTimeFilter closedAt) {
        this.closedAt = closedAt;
    }

    public LongFilter getAuditLinesId() {
        return auditLinesId;
    }

    public LongFilter auditLinesId() {
        if (auditLinesId == null) {
            auditLinesId = new LongFilter();
        }
        return auditLinesId;
    }

    public void setAuditLinesId(LongFilter auditLinesId) {
        this.auditLinesId = auditLinesId;
    }

    public LongFilter getWarehouseId() {
        return warehouseId;
    }

    public LongFilter warehouseId() {
        if (warehouseId == null) {
            warehouseId = new LongFilter();
        }
        return warehouseId;
    }

    public void setWarehouseId(LongFilter warehouseId) {
        this.warehouseId = warehouseId;
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
        final StockAuditCriteria that = (StockAuditCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(status, that.status) &&
            Objects.equals(theoreticalCount, that.theoreticalCount) &&
            Objects.equals(physicalCount, that.physicalCount) &&
            Objects.equals(discrepancyCount, that.discrepancyCount) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(auditorLogin, that.auditorLogin) &&
            Objects.equals(startedAt, that.startedAt) &&
            Objects.equals(closedAt, that.closedAt) &&
            Objects.equals(auditLinesId, that.auditLinesId) &&
            Objects.equals(warehouseId, that.warehouseId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            tenantId,
            status,
            theoreticalCount,
            physicalCount,
            discrepancyCount,
            notes,
            auditorLogin,
            startedAt,
            closedAt,
            auditLinesId,
            warehouseId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockAuditCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tenantId != null ? "tenantId=" + tenantId + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (theoreticalCount != null ? "theoreticalCount=" + theoreticalCount + ", " : "") +
            (physicalCount != null ? "physicalCount=" + physicalCount + ", " : "") +
            (discrepancyCount != null ? "discrepancyCount=" + discrepancyCount + ", " : "") +
            (notes != null ? "notes=" + notes + ", " : "") +
            (auditorLogin != null ? "auditorLogin=" + auditorLogin + ", " : "") +
            (startedAt != null ? "startedAt=" + startedAt + ", " : "") +
            (closedAt != null ? "closedAt=" + closedAt + ", " : "") +
            (auditLinesId != null ? "auditLinesId=" + auditLinesId + ", " : "") +
            (warehouseId != null ? "warehouseId=" + warehouseId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
