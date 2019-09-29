package com.revolut.mts.dto;

import java.time.Instant;
import java.util.Objects;

final public class Transaction {

    private Long id;
    private TransactionState state;
    private String userName;
    private MoneyAmount amount;
    private Instant creationTime;

    public Transaction(Long id, TransactionState state, String userName, MoneyAmount amount, Instant creationTime) {
        this.id = id;
        this.state = state;
        this.userName = userName;
        this.amount = amount;
        this.creationTime = creationTime;
    }

    public TransactionState getState() {
        return state;
    }

    public String getUserName() {
        return userName;
    }

    public MoneyAmount getAmount() {
        return amount;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
                state == that.state &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(creationTime, that.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, userName, amount, creationTime);
    }
}
