package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class Transaction {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("sender")
    private String sender;
    @JsonProperty("receiver")
    private String receiver;
    @JsonProperty("source_money")
    private MoneyAmount sourceMoney;
    @JsonProperty("destination_money")
    private MoneyAmount destinationMoney;
    @JsonProperty("creation_time")
    private Instant creationTime;

    public Transaction(Integer id, String sender, String receiver, MoneyAmount sourceMoney, MoneyAmount destinationMoney, Instant creationTime) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.sourceMoney = sourceMoney;
        this.destinationMoney = destinationMoney;
        this.creationTime = creationTime;
    }
}
