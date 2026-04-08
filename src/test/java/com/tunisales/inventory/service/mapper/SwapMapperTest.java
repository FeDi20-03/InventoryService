package com.tunisales.inventory.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SwapMapperTest {

    private SwapMapper swapMapper;

    @BeforeEach
    public void setUp() {
        swapMapper = new SwapMapperImpl();
    }
}
