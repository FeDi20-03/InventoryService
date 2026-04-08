package com.tunisales.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockAuditDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockAuditDTO.class);
        StockAuditDTO stockAuditDTO1 = new StockAuditDTO();
        stockAuditDTO1.setId(1L);
        StockAuditDTO stockAuditDTO2 = new StockAuditDTO();
        assertThat(stockAuditDTO1).isNotEqualTo(stockAuditDTO2);
        stockAuditDTO2.setId(stockAuditDTO1.getId());
        assertThat(stockAuditDTO1).isEqualTo(stockAuditDTO2);
        stockAuditDTO2.setId(2L);
        assertThat(stockAuditDTO1).isNotEqualTo(stockAuditDTO2);
        stockAuditDTO1.setId(null);
        assertThat(stockAuditDTO1).isNotEqualTo(stockAuditDTO2);
    }
}
