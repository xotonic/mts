package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class JSONResponse<RESULT> {

    @JsonProperty("result")
    private RESULT result;

    @JsonProperty("error")
    JSONError error;

    public JSONResponse(RESULT result) {
        this.result = result;
        this.error = null;
    }

    public static EmptyResponse failure(JSONError error) {
        var failure = new EmptyResponse();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONResponse<?> that = (JSONResponse<?>) o;
        return Objects.equals(result, that.result) &&
                Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, error);
    }

    @Override
    public String toString() {
        return "JSONResponse{" +
                "result=" + result +
                ", error=" + error +
                '}';
    }
}
