package com.tunisales.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockAuditTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockAudit.class);
        StockAudit stockAudit1 = new StockAudit();
        stockAudit1.setId(1L);
        StockAudit stockAudit2 = new StockAudit();
        stockAudit2.setId(stockAudit1.getId());
        assertThat(stockAudit1).isEqualTo(stockAudit2);
        stockAudit2.setId(2L);
        assertThat(stockAudit1).isNotEqualTo(stockAudit2);
        stockAudit1.setId(null);
        assertThat(stockAudit1).isNotEqualTo(stockAudit2);
    }
}
