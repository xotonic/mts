package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Body<RESULT> {

    @JsonProperty("result")
    private RESULT result;

    public Body(RESULT result) {
        this.result = result;
    }

    public boolean succeed() {
        return result != null;
    }

    public RESULT result() {
        return result;
    }
}
