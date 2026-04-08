package com.tunisales.inventory.service.mapper;

import com.tunisales.inventory.domain.StockAudit;
import com.tunisales.inventory.domain.StockAuditLine;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.service.dto.StockAuditDTO;
import com.tunisales.inventory.service.dto.StockAuditLineDTO;
import com.tunisales.inventory.service.dto.StockItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockAuditLine} and its DTO {@link StockAuditLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockAuditLineMapper extends EntityMapper<StockAuditLineDTO, StockAuditLine> {
    @Mapping(target = "stockItem", source = "stockItem", qualifiedByName = "stockItemImei")
    @Mapping(target = "audit", source = "audit", qualifiedByName = "stockAuditId")
    StockAuditLineDTO toDto(StockAuditLine s);

    @Named("stockItemImei")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "imei", source = "imei")
    StockItemDTO toDtoStockItemImei(StockItem stockItem);

    @Named("stockAuditId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StockAuditDTO toDtoStockAuditId(StockAudit stockAudit);
}
