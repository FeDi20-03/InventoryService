package com.tunisales.inventory.service.mapper;

import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.service.dto.StockItemDTO;
import com.tunisales.inventory.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockItem} and its DTO {@link StockItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockItemMapper extends EntityMapper<StockItemDTO, StockItem> {
    @Mapping(target = "warehouse", source = "warehouse", qualifiedByName = "warehouseName")
    StockItemDTO toDto(StockItem s);

    @Named("warehouseName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WarehouseDTO toDtoWarehouseName(Warehouse warehouse);
}
