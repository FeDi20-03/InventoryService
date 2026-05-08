package com.tunisales.inventory.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ImeiValidatorTest {

    @Test
    void rejectsNull() {
        assertThat(ImeiValidator.isValid(null)).isFalse();
    }

    @Test
    void rejectsWrongLength() {
        assertThat(ImeiValidator.isValid("")).isFalse();
        assertThat(ImeiValidator.isValid("1234567890")).isFalse();
        assertThat(ImeiValidator.isValid("4901542032375180")).isFalse(); // 16 digits
        assertThat(ImeiValidator.isValid("49015420323751")).isFalse(); // 14 digits
    }

    @Test
    void rejectsNonDigits() {
        assertThat(ImeiValidator.isValid("49015420323751A")).isFalse();
        assertThat(ImeiValidator.isValid("AAAAAAAAAAAAAAA")).isFalse();
        assertThat(ImeiValidator.isValid("49015420323751 ")).isFalse();
    }

    @Test
    void rejectsBadChecksum() {
        // Same digits as the valid IMEI below, but with the check digit incremented.
        assertThat(ImeiValidator.isValid("490154203237519")).isFalse();
        assertThat(ImeiValidator.isValid("123456789012345")).isFalse();
    }

    @Test
    void acceptsValidImeis() {
        // Well-known test IMEI.
        assertThat(ImeiValidator.isValid("490154203237518")).isTrue();
        // Other valid IMEIs (verified by computing the Luhn check digit).
        assertThat(ImeiValidator.isValid("356938035643809")).isTrue();
        assertThat(ImeiValidator.isValid("352099001761481")).isTrue();
    }
}
