package com.tunisales.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockAuditLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockAuditLine.class);
        StockAuditLine stockAuditLine1 = new StockAuditLine();
        stockAuditLine1.setId(1L);
        StockAuditLine stockAuditLine2 = new StockAuditLine();
        stockAuditLine2.setId(stockAuditLine1.getId());
        assertThat(stockAuditLine1).isEqualTo(stockAuditLine2);
        stockAuditLine2.setId(2L);
        assertThat(stockAuditLine1).isNotEqualTo(stockAuditLine2);
        stockAuditLine1.setId(null);
        assertThat(stockAuditLine1).isNotEqualTo(stockAuditLine2);
    }
}
