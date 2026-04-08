package com.tunisales.inventory.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockItemMapperTest {

    private StockItemMapper stockItemMapper;

    @BeforeEach
    public void setUp() {
        stockItemMapper = new StockItemMapperImpl();
    }
}
