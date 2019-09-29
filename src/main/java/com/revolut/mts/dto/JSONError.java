package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class JSONError {

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    public JSONError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONError error = (JSONError) o;
        return code == error.code &&
                Objects.equals(message, error.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
