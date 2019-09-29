package com.revolut.mts.dto;

import java.time.Instant;

final public class Deposit {
    private MoneyAmount amount;
    private Instant depositTime;

    public MoneyAmount getAmount() {
        return amount;
    }

    public Instant getDepositTime() {
        return depositTime;
    }
}
