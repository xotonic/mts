package com.revolut.mts.util;


import com.revolut.mts.dto.MoneyAmount;

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

    public static boolean validateCurrencies(MoneyAmount... moneyAmount) {
        for (var m : moneyAmount) {
            if (!Currencies.isSupported(m.getCurrency())) {
                return false;
            }
        }
        return true;
    }
}
