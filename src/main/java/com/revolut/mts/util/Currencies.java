package com.revolut.mts.util;


public enum Currencies {
    USD("USD"),
    EUR("EUR"),
    BTC("BTC"),
    ETH("ETH");

    private String code;

    Currencies(String code) {
        this.code = code;
    }

    public static boolean isSupported(String code) {
        for (var supported : values()) {
            if (supported.code.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateCurrencies(String... currencies) {
        for (var c : currencies) {
            if (!Currencies.isSupported(c)) {
                return false;
            }
        }
        return true;
    }
}
