package com.tunisales.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockAuditLineDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockAuditLineDTO.class);
        StockAuditLineDTO stockAuditLineDTO1 = new StockAuditLineDTO();
        stockAuditLineDTO1.setId(1L);
        StockAuditLineDTO stockAuditLineDTO2 = new StockAuditLineDTO();
        assertThat(stockAuditLineDTO1).isNotEqualTo(stockAuditLineDTO2);
        stockAuditLineDTO2.setId(stockAuditLineDTO1.getId());
        assertThat(stockAuditLineDTO1).isEqualTo(stockAuditLineDTO2);
        stockAuditLineDTO2.setId(2L);
        assertThat(stockAuditLineDTO1).isNotEqualTo(stockAuditLineDTO2);
        stockAuditLineDTO1.setId(null);
        assertThat(stockAuditLineDTO1).isNotEqualTo(stockAuditLineDTO2);
    }
}
