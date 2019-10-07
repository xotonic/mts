package com.revolut.mts.util;

/**
 * A hardcoded list of supported currencies.
 * Primarily used for validation input parameters
 */
public enum Currencies {
    USD("USD"),
    EUR("EUR"),
    BTC("BTC"),
    ETH("ETH");

    private String code;

    Currencies(String code) {
        this.code = code;
    }

    /**
     * Check if currency is supported in the application
     * @param code Currency code
     * @return True if the currency is supported
     */
    public static boolean isSupported(String code) {
        for (var supported : values()) {
            if (supported.code.equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check the given list of currency codes is supported in this application
     * @param currencies List of input currencies
     * @return true if all input currencies are supported
     */
    public static boolean validateCurrencies(String... currencies) {
        for (var c : currencies) {
            if (!Currencies.isSupported(c)) {
                return false;
            }
        }
        return true;
    }
}
