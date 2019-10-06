package com.revolut.mts.http;

import com.revolut.mts.dto.EmptyBody;
import com.revolut.mts.http.routing.RoutePath;

public interface RequestContext extends ResponseProvider, RoutePath {

    default <T> HResponse<T> ok(T body) {
        return respond(HStatus.OK, body);
    }

    default HResponse<EmptyBody> ok() {
        return respond(HStatus.OK, new EmptyBody());
    }

    default <T> HResponse<T> created(T body) {
        return respond(HStatus.CREATED, body);
    }

    default <T> HResponse<T> conflict(String message) {
        return error(HStatus.CONFLICT, message);
    }

    default <T> HResponse<T> notFound(String message) {
        return error(HStatus.NOT_FOUND, message);
    }
}
