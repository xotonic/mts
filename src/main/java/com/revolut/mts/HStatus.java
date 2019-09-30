package com.revolut.mts;

import fi.iki.elonen.NanoHTTPD;

public enum HStatus implements NanoHTTPD.Response.IStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_ERROR(500, "Internal Server Error"),
    ;

    private final int requestStatus;

    private final String description;

    HStatus(int requestStatus, String description) {
        this.requestStatus = requestStatus;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return  this.description;
    }

    @Override
    public int getRequestStatus() {
        return this.requestStatus;
    }
}
