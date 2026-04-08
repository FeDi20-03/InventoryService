package com.tunisales.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockItemDTO.class);
        StockItemDTO stockItemDTO1 = new StockItemDTO();
        stockItemDTO1.setId(1L);
        StockItemDTO stockItemDTO2 = new StockItemDTO();
        assertThat(stockItemDTO1).isNotEqualTo(stockItemDTO2);
        stockItemDTO2.setId(stockItemDTO1.getId());
        assertThat(stockItemDTO1).isEqualTo(stockItemDTO2);
        stockItemDTO2.setId(2L);
        assertThat(stockItemDTO1).isNotEqualTo(stockItemDTO2);
        stockItemDTO1.setId(null);
        assertThat(stockItemDTO1).isNotEqualTo(stockItemDTO2);
    }
}
