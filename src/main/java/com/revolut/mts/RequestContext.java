package com.revolut.mts;

public interface RequestContext extends ResponseProvider, RoutePath {

    default <T> HResponse<T> ok(T body) {
        return respond(HStatus.OK, body);
    }
}
