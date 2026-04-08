package com.tunisales.inventory.service.criteria;

import com.tunisales.inventory.domain.enumeration.SwapStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.inventory.domain.Swap} entity. This class is used
 * in {@link com.tunisales.inventory.web.rest.SwapResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /swaps?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SwapCriteria implements Serializable, Criteria {

    /**
     * Class for filtering SwapStatus
     */
    public static class SwapStatusFilter extends Filter<SwapStatus> {

        public SwapStatusFilter() {}

        public SwapStatusFilter(SwapStatusFilter filter) {
            super(filter);
        }

        @Override
        public SwapStatusFilter copy() {
            return new SwapStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tenantId;

    private LongFilter clientId;

    private StringFilter clientName;

    private SwapStatusFilter status;

    private StringFilter reason;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter resolvedAt;

    private ZonedDateTimeFilter updatedAt;

    private LongFilter outgoingItemId;

    private LongFilter incomingItemId;

    private Boolean distinct;

    public SwapCriteria() {}

    public SwapCriteria(SwapCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tenantId = other.tenantId == null ? null : other.tenantId.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
        this.clientName = other.clientName == null ? null : other.clientName.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.reason = other.reason == null ? null : other.reason.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.resolvedAt = other.resolvedAt == null ? null : other.resolvedAt.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.outgoingItemId = other.outgoingItemId == null ? null : other.outgoingItemId.copy();
        this.incomingItemId = other.incomingItemId == null ? null : other.incomingItemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SwapCriteria copy() {
        return new SwapCriteria(this);
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

    public LongFilter getClientId() {
        return clientId;
    }

    public LongFilter clientId() {
        if (clientId == null) {
            clientId = new LongFilter();
        }
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
    }

    public StringFilter getClientName() {
        return clientName;
    }

    public StringFilter clientName() {
        if (clientName == null) {
            clientName = new StringFilter();
        }
        return clientName;
    }

    public void setClientName(StringFilter clientName) {
        this.clientName = clientName;
    }

    public SwapStatusFilter getStatus() {
        return status;
    }

    public SwapStatusFilter status() {
        if (status == null) {
            status = new SwapStatusFilter();
        }
        return status;
    }

    public void setStatus(SwapStatusFilter status) {
        this.status = status;
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

    public ZonedDateTimeFilter getResolvedAt() {
        return resolvedAt;
    }

    public ZonedDateTimeFilter resolvedAt() {
        if (resolvedAt == null) {
            resolvedAt = new ZonedDateTimeFilter();
        }
        return resolvedAt;
    }

    public void setResolvedAt(ZonedDateTimeFilter resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public ZonedDateTimeFilter getUpdatedAt() {
        return updatedAt;
    }

    public ZonedDateTimeFilter updatedAt() {
        if (updatedAt == null) {
            updatedAt = new ZonedDateTimeFilter();
        }
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTimeFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getOutgoingItemId() {
        return outgoingItemId;
    }

    public LongFilter outgoingItemId() {
        if (outgoingItemId == null) {
            outgoingItemId = new LongFilter();
        }
        return outgoingItemId;
    }

    public void setOutgoingItemId(LongFilter outgoingItemId) {
        this.outgoingItemId = outgoingItemId;
    }

    public LongFilter getIncomingItemId() {
        return incomingItemId;
    }

    public LongFilter incomingItemId() {
        if (incomingItemId == null) {
            incomingItemId = new LongFilter();
        }
        return incomingItemId;
    }

    public void setIncomingItemId(LongFilter incomingItemId) {
        this.incomingItemId = incomingItemId;
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
        final SwapCriteria that = (SwapCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(clientName, that.clientName) &&
            Objects.equals(status, that.status) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(resolvedAt, that.resolvedAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(outgoingItemId, that.outgoingItemId) &&
            Objects.equals(incomingItemId, that.incomingItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            tenantId,
            clientId,
            clientName,
            status,
            reason,
            createdAt,
            resolvedAt,
            updatedAt,
            outgoingItemId,
            incomingItemId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SwapCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tenantId != null ? "tenantId=" + tenantId + ", " : "") +
            (clientId != null ? "clientId=" + clientId + ", " : "") +
            (clientName != null ? "clientName=" + clientName + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (reason != null ? "reason=" + reason + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (resolvedAt != null ? "resolvedAt=" + resolvedAt + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (outgoingItemId != null ? "outgoingItemId=" + outgoingItemId + ", " : "") +
            (incomingItemId != null ? "incomingItemId=" + incomingItemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
