package com.revolut.mts;

import java.util.Arrays;

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
        for (var c : values()) {
            if (c.code.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static Currencies parse(String code) {
        return Arrays.stream(values()).filter(v -> v.code.equals(code)).findFirst().orElseThrow();
    }
}
