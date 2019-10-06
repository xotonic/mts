package com.revolut.mts.dto;

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

    public NewTransactionBuilder setSourceMoney(MoneyAmount sourceMoney) {
        this.sourceMoney = sourceMoney;
        return this;
    }

    public NewTransactionBuilder setDestinationMoney(MoneyAmount destinationMoney) {
        this.destinationMoney = destinationMoney;
        return this;
    }

    public NewTransaction createNewTransaction() {
        return new NewTransaction(sender, receiver, sourceMoney, destinationMoney);
    }
}