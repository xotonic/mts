package com.revolut.mts.http;


/**
 * The only used response statuses
 */
public enum HStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),

    BAD_REQUEST(400, "Bad request"),
    NOT_FOUND(404, "Not found"),
    METHOD_NOT_ALLOWED(405, "Method not allowed"),
    CONFLICT(409, "Conflict"),

    INTERNAL_ERROR(500, "Internal error");

    private int code;
    private String description;

    HStatus(int status, String description) {
        this.code = status;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

}
