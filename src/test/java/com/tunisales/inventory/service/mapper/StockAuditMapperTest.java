package com.tunisales.inventory.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockAuditMapperTest {

    private StockAuditMapper stockAuditMapper;

    @BeforeEach
    public void setUp() {
        stockAuditMapper = new StockAuditMapperImpl();
    }
}
