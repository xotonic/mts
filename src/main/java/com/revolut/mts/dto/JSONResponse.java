package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

final public class JSONResponse<RESULT> {

    @JsonProperty("result")
    private RESULT result;

    @JsonProperty("error")
    private JSONError error;

    public JSONResponse(RESULT result) {
        this.result = result;
        this.error = null;
    }

    public static <T> JSONResponse<T> failure(JSONError error) {
        JSONResponse<T> failure = new JSONResponse<>(null);
        failure.error = error;
        return failure;
    }

    public boolean succeed() {
        return result != null;
    }

    public boolean failed() {
        return error != null;
    }

    public RESULT result() {
        return result;
    }
    public JSONError error() {
        return error;
    }
}
