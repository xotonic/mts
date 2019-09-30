package com.revolut.mts;

public class RequestContext {

    private ResponseProvider responseProvider;

    public RequestContext(ResponseProvider responseProvider) {
        this.responseProvider = responseProvider;
    }

    public HResponse success(HStatus status, Object body) {
        return responseProvider.success(status, body);
    }

    public HResponse ok(Object body) {
        return responseProvider.success(HStatus.OK, body);
    }

    public HResponse error(HStatus status, String message) {
        return responseProvider.error(status, message);
    }

    public HResponse error(HStatus status) {
        return responseProvider.error(status);
    }

}
