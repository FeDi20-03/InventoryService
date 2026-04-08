package com.tunisales.inventory.service.mapper;

import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.Swap;
import com.tunisales.inventory.service.dto.StockItemDTO;
import com.tunisales.inventory.service.dto.SwapDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Swap} and its DTO {@link SwapDTO}.
 */
@Mapper(componentModel = "spring")
public interface SwapMapper extends EntityMapper<SwapDTO, Swap> {
    @Mapping(target = "outgoingItem", source = "outgoingItem", qualifiedByName = "stockItemImei")
    @Mapping(target = "incomingItem", source = "incomingItem", qualifiedByName = "stockItemImei")
    SwapDTO toDto(Swap s);

    @Named("stockItemImei")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "imei", source = "imei")
    StockItemDTO toDtoStockItemImei(StockItem stockItem);
}
