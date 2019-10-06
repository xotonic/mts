package com.revolut.mts.service;

import java.math.BigDecimal;

public class TxParams {
    private String sender;
    private String receiver;
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal sourceAmount;
    private BigDecimal targetAmount;

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getSourceAmount() {
        return sourceAmount;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    static public class Builder {

        private String sender;
        private String receiver;
        private String sourceCurrency;
        private String targetCurrency;
        private BigDecimal sourceAmount;
        private BigDecimal targetAmount;

        public Builder setSource(String userName, BigDecimal amount, String currency) {
            sender = userName;
            sourceAmount = amount;
            sourceCurrency = currency;
            return this;
        }
        public Builder setTarget(String userName, BigDecimal amount, String currency) {
            receiver = userName;
            targetAmount = amount;
            targetCurrency = currency;
            return this;
        }

        public TxParams build() {
            TxParams o = new TxParams();
            o.sender = sender;
            o.receiver = receiver;
            o.sourceCurrency = sourceCurrency;
            o.targetCurrency = targetCurrency;
            o.sourceAmount = sourceAmount;
            o.targetAmount = targetAmount;
            return o;
        }
    }
}
