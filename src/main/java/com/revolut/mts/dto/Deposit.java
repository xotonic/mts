package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

final public class Deposit {
    @JsonProperty("username")
    private String userName;
    @JsonProperty("amount")
    private MoneyAmount amount;

    public Deposit(String userName, MoneyAmount amount) {
        this.userName = userName;
        this.amount = amount;
    }

    public String getUserName() {
        return userName;
    }

    public MoneyAmount getAmount() {
        return amount;
    }
}
