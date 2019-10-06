package com.revolut.mts.http;

import fi.iki.elonen.NanoHTTPD;

public enum HStatus {
    OK(NanoHTTPD.Response.Status.OK),
    CREATED(NanoHTTPD.Response.Status.CREATED),
    NOT_FOUND(NanoHTTPD.Response.Status.NOT_FOUND),
    CONFLICT(NanoHTTPD.Response.Status.CONFLICT),
    BAD_REQUEST(NanoHTTPD.Response.Status.BAD_REQUEST),
    METHOD_NOT_ALLOWED(NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED),
    INTERNAL_ERROR(NanoHTTPD.Response.Status.INTERNAL_ERROR),
    ;

    NanoHTTPD.Response.IStatus status;

    HStatus(NanoHTTPD.Response.IStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return status.getDescription();
    }

    public int getRequestStatus() {
        return status.getRequestStatus();
    }

    public NanoHTTPD.Response.IStatus getStatus() {
        return status;
    }
}
