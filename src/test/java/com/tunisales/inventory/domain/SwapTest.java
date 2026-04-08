package com.tunisales.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SwapTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Swap.class);
        Swap swap1 = new Swap();
        swap1.setId(1L);
        Swap swap2 = new Swap();
        swap2.setId(swap1.getId());
        assertThat(swap1).isEqualTo(swap2);
        swap2.setId(2L);
        assertThat(swap1).isNotEqualTo(swap2);
        swap1.setId(null);
        assertThat(swap1).isNotEqualTo(swap2);
    }
}
