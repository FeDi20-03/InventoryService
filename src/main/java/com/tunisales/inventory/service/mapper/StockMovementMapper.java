package com.tunisales.inventory.service.mapper;

import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.StockMovement;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.service.dto.StockItemDTO;
import com.tunisales.inventory.service.dto.StockMovementDTO;
import com.tunisales.inventory.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockMovement} and its DTO {@link StockMovementDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockMovementMapper extends EntityMapper<StockMovementDTO, StockMovement> {
    @Mapping(target = "fromWarehouse", source = "fromWarehouse", qualifiedByName = "warehouseName")
    @Mapping(target = "toWarehouse", source = "toWarehouse", qualifiedByName = "warehouseName")
    @Mapping(target = "stockItem", source = "stockItem", qualifiedByName = "stockItemImei")
    StockMovementDTO toDto(StockMovement s);

    @Named("warehouseName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WarehouseDTO toDtoWarehouseName(Warehouse warehouse);

    @Named("stockItemImei")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "imei", source = "imei")
    StockItemDTO toDtoStockItemImei(StockItem stockItem);
}
