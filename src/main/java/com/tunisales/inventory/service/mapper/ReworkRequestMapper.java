package com.tunisales.inventory.service.mapper;

import com.tunisales.inventory.domain.ReworkRequest;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.service.dto.ReworkRequestDTO;
import com.tunisales.inventory.service.dto.StockItemDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link ReworkRequest} and its DTO {@link ReworkRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReworkRequestMapper extends EntityMapper<ReworkRequestDTO, ReworkRequest> {
    @Mapping(target = "stockItem", source = "stockItem", qualifiedByName = "stockItemId")
    ReworkRequestDTO toDto(ReworkRequest s);

    @Named("stockItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "imei", source = "imei")
    StockItemDTO toDtoStockItemId(StockItem stockItem);
}
