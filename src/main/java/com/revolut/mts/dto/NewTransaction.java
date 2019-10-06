package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

final public class NewTransaction {

    @JsonProperty("sender")
    private String sender;
    @JsonProperty("receiver")
    private String receiver;
    @JsonProperty("source_money")
    private MoneyAmount sourceMoney;
    @JsonProperty("destination_money")
    private MoneyAmount destinationMoney;

    public NewTransaction() {}

    public NewTransaction(String sender, String receiver, MoneyAmount sourceMoney, MoneyAmount destinationMoney) {
        this.sender = sender;
        this.receiver = receiver;
        this.sourceMoney = sourceMoney;
        this.destinationMoney = destinationMoney;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public MoneyAmount getSourceMoney() {
        return sourceMoney;
    }

    public MoneyAmount getDestinationMoney() {
        return destinationMoney;
    }
}
