package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransactionState {
    @JsonProperty("new")
    NEW,
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("finished")
    FINISHED
}
