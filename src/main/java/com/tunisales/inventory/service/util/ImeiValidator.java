package com.tunisales.inventory.service.util;

/**
 * Utility for validating IMEI (International Mobile Equipment Identity) numbers.
 *
 * <p>An IMEI is a 15-digit identifier whose final digit is a Luhn check digit
 * computed over the first 14 digits.</p>
 */
public final class ImeiValidator {

    private static final int IMEI_LENGTH = 15;

    private ImeiValidator() {}

    /**
     * Tests whether the supplied string is a syntactically valid IMEI.
     *
     * @param imei candidate IMEI; may be {@code null}
     * @return {@code true} if {@code imei} contains exactly 15 decimal digits
     *         and passes the Luhn checksum, {@code false} otherwise
     */
    public static boolean isValid(String imei) {
        if (imei == null || imei.length() != IMEI_LENGTH) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < IMEI_LENGTH; i++) {
            char c = imei.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
            int digit = c - '0';
            // Luhn: double every second digit starting from the rightmost-but-one.
            // For a 15-digit number, that means doubling positions 0,2,4,...,12 (zero-based, left-to-right).
            if (i % 2 == 0) {
                // not doubled
                sum += digit;
            } else {
                int doubled = digit * 2;
                sum += (doubled > 9) ? doubled - 9 : doubled;
            }
        }
        return sum % 10 == 0;
    }
}
