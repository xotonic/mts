package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionId {
    @JsonProperty("id")
    private Integer id;

    public TransactionId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
