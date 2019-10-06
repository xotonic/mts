package com.revolut.mts.service;

import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.dto.Transaction;

import java.time.Instant;

public class TransactionBuilder {
    private Integer id;
    private String sender;
    private String receiver;
    private MoneyAmount sourceMoney;
    private MoneyAmount destinationMoney;
    private Instant creationTime;

    public TransactionBuilder setId(Integer id) {
        this.id = id;
        return this;
    }

    public TransactionBuilder setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public TransactionBuilder setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public TransactionBuilder setSourceMoney(MoneyAmount sourceMoney) {
        this.sourceMoney = sourceMoney;
        return this;
    }

    public TransactionBuilder setDestinationMoney(MoneyAmount destinationMoney) {
        this.destinationMoney = destinationMoney;
        return this;
    }

    public TransactionBuilder setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public Transaction createTransaction() {
        return new Transaction(id, sender, receiver, sourceMoney, destinationMoney, creationTime);
    }
}