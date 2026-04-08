package com.tunisales.inventory.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockAuditLineMapperTest {

    private StockAuditLineMapper stockAuditLineMapper;

    @BeforeEach
    public void setUp() {
        stockAuditLineMapper = new StockAuditLineMapperImpl();
    }
}
