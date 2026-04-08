package com.tunisales.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockMovementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockMovement.class);
        StockMovement stockMovement1 = new StockMovement();
        stockMovement1.setId(1L);
        StockMovement stockMovement2 = new StockMovement();
        stockMovement2.setId(stockMovement1.getId());
        assertThat(stockMovement1).isEqualTo(stockMovement2);
        stockMovement2.setId(2L);
        assertThat(stockMovement1).isNotEqualTo(stockMovement2);
        stockMovement1.setId(null);
        assertThat(stockMovement1).isNotEqualTo(stockMovement2);
    }
}
