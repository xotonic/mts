package com.revolut.mts.util;

import com.revolut.mts.HResponse;
import com.revolut.mts.HStatus;
import com.revolut.mts.RequestContext;
import fi.iki.elonen.NanoHTTPD;

public class TestContext implements RequestContext {

    private boolean isSuccess;
    private Object lastBody;
    private HStatus lastStatus;
    private String lastErrorMessage;

    @Override
    public <T> HResponse<T> respond(HStatus status, T body) {
        isSuccess = true;
        lastStatus = status;
        lastBody = body;
        return new HResponse<>(NanoHTTPD.newFixedLengthResponse(status.getStatus(), "application/json", "{}"));
    }

    @Override
    public <T> HResponse<T> error(HStatus status, String message) {
        isSuccess = false;
        lastStatus = status;
        lastErrorMessage = message;
        return new HResponse<>(NanoHTTPD.newFixedLengthResponse(status.getStatus(), "application/json", "{}"));
    }

    public <T> T getLastBody(Class<T> clazz) {
        return clazz.cast(lastBody);
    }

    public HStatus getLastStatus() {
        return lastStatus;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public String getString(int index) {
        return null;
    }

    @Override
    public int getInt(int index) {
        return 0;
    }
}
