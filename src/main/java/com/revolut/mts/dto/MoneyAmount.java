package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

final public class MoneyAmount {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("amount")
    private BigDecimal amount;

    public MoneyAmount() {
    }

    public MoneyAmount(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
