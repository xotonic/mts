package com.revolut.mts.http;


public enum HStatus {
    OK(200),
    CREATED(201),

    BAD_REQUEST(400),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    CONFLICT(409),

    INTERNAL_ERROR(500);

    private int code;
    private String description;

    HStatus(int status) {
        this.code = status;
        this.description = "To do add description";
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

}
