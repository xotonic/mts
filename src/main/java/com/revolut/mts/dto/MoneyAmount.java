package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

final public class MoneyAmount {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("amount")
    private BigDecimal amount;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoneyAmount that = (MoneyAmount) o;
        return Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return "MoneyAmount{" +
                "amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
