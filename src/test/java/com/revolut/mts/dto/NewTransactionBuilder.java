package com.revolut.mts.dto;

import java.math.BigDecimal;

public class NewTransactionBuilder {
    private String sender;
    private String receiver;
    private MoneyAmount sourceMoney;
    private MoneyAmount destinationMoney;

    public NewTransactionBuilder setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public NewTransactionBuilder setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public NewTransactionBuilder setSourceMoney(double amount, String currency) {
        this.sourceMoney = new MoneyAmount(new BigDecimal(amount), currency);
        return this;
    }

    public NewTransactionBuilder setDestinationMoney(double amount, String currency) {
        this.destinationMoney = new MoneyAmount(new BigDecimal(amount), currency);
        return this;
    }

    public NewTransaction createNewTransaction() {
        return new NewTransaction(sender, receiver, sourceMoney, destinationMoney);
    }
}