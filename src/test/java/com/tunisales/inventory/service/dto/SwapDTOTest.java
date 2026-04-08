package com.tunisales.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SwapDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SwapDTO.class);
        SwapDTO swapDTO1 = new SwapDTO();
        swapDTO1.setId(1L);
        SwapDTO swapDTO2 = new SwapDTO();
        assertThat(swapDTO1).isNotEqualTo(swapDTO2);
        swapDTO2.setId(swapDTO1.getId());
        assertThat(swapDTO1).isEqualTo(swapDTO2);
        swapDTO2.setId(2L);
        assertThat(swapDTO1).isNotEqualTo(swapDTO2);
        swapDTO1.setId(null);
        assertThat(swapDTO1).isNotEqualTo(swapDTO2);
    }
}
