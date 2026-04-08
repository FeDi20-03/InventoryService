package com.tunisales.inventory.service.mapper;

import com.tunisales.inventory.domain.StockAudit;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.service.dto.StockAuditDTO;
import com.tunisales.inventory.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockAudit} and its DTO {@link StockAuditDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockAuditMapper extends EntityMapper<StockAuditDTO, StockAudit> {
    @Mapping(target = "warehouse", source = "warehouse", qualifiedByName = "warehouseName")
    StockAuditDTO toDto(StockAudit s);

    @Named("warehouseName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WarehouseDTO toDtoWarehouseName(Warehouse warehouse);
}
