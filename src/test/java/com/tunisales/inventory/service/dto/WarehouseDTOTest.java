package com.tunisales.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseDTO.class);
        WarehouseDTO warehouseDTO1 = new WarehouseDTO();
        warehouseDTO1.setId(1L);
        WarehouseDTO warehouseDTO2 = new WarehouseDTO();
        assertThat(warehouseDTO1).isNotEqualTo(warehouseDTO2);
        warehouseDTO2.setId(warehouseDTO1.getId());
        assertThat(warehouseDTO1).isEqualTo(warehouseDTO2);
        warehouseDTO2.setId(2L);
        assertThat(warehouseDTO1).isNotEqualTo(warehouseDTO2);
        warehouseDTO1.setId(null);
        assertThat(warehouseDTO1).isNotEqualTo(warehouseDTO2);
    }
}
