package com.tunisales.inventory.service.dto;

import com.tunisales.inventory.domain.enumeration.AuditStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.tunisales.inventory.domain.StockAudit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockAuditDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    private AuditStatus status;

    @Min(value = 0)
    private Integer theoreticalCount;

    @Min(value = 0)
    private Integer physicalCount;

    @Min(value = 0)
    private Integer discrepancyCount;

    @Size(max = 2000)
    private String notes;

    @NotNull
    @Size(max = 100)
    private String auditorLogin;

    @NotNull
    private ZonedDateTime startedAt;

    private ZonedDateTime closedAt;

    private WarehouseDTO warehouse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public AuditStatus getStatus() {
        return status;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public Integer getTheoreticalCount() {
        return theoreticalCount;
    }

    public void setTheoreticalCount(Integer theoreticalCount) {
        this.theoreticalCount = theoreticalCount;
    }

    public Integer getPhysicalCount() {
        return physicalCount;
    }

    public void setPhysicalCount(Integer physicalCount) {
        this.physicalCount = physicalCount;
    }

    public Integer getDiscrepancyCount() {
        return discrepancyCount;
    }

    public void setDiscrepancyCount(Integer discrepancyCount) {
        this.discrepancyCount = discrepancyCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAuditorLogin() {
        return auditorLogin;
    }

    public void setAuditorLogin(String auditorLogin) {
        this.auditorLogin = auditorLogin;
    }

    public ZonedDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(ZonedDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public ZonedDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(ZonedDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public WarehouseDTO getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseDTO warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockAuditDTO)) {
            return false;
        }

        StockAuditDTO stockAuditDTO = (StockAuditDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockAuditDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockAuditDTO{" +
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
            ", warehouse=" + getWarehouse() +
            "}";
    }
}
