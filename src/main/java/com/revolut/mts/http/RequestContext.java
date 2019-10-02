package com.revolut.mts.http;

import com.revolut.mts.http.routing.RoutePath;

public interface RequestContext extends ResponseProvider, RoutePath {

    default <T> HResponse<T> ok(T body) {
        return respond(HStatus.OK, body);
    }
}
