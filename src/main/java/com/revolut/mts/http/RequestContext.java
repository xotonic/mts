package com.revolut.mts.http;

import com.revolut.mts.dto.EmptyBody;
import com.revolut.mts.http.routing.RoutePath;

/**
 * Its instance is passed to request handler
 * Provides functionality to build responses, parsing request object and URI arguments
 */
public interface RequestContext extends ResponseProvider, RoutePath, RequestProvider {

    /**
     * Respond with code 200 and given payload.
     * The payload will be converted into JSON representation.
     * @param body instance of object representing response body
     * @param <T> type of the object
     * @return HTTP response
     */
    default <T> HResponse<T> ok(T body) {
        return respond(HStatus.OK, body);
    }

    /**
     * Respond with code 200 and empty payload.
     * @return HTTP response
     */
    default HResponse<EmptyBody> ok() {
        return respond(HStatus.OK, new EmptyBody());
    }

    /**
     * Respond with code 201 and given payload.
     * The payload will be converted into JSON representation.
     * @param body instance of object representing response body
     * @param <T> type of the object
     * @return HTTP response
     */
    default <T> HResponse<T> created(T body) {
        return respond(HStatus.CREATED, body);
    }

    /**
     * Respond with code 409 and given error message.
     * @param message A simple string describing the error for user
     * @return HTTP response
     */
    default <T> HResponse<T> conflict(String message) {
        return error(HStatus.CONFLICT, message);
    }

    /**
     * Respond with code 404 and given error message.
     * @param message A simple string describing the error for user
     * @return HTTP response
     */
    default <T> HResponse<T> notFound(String message) {
        return error(HStatus.NOT_FOUND, message);
    }

    /**
     * Respond with code 400 and given error message.
     * @param message A simple string describing the error for user
     * @return HTTP response
     */
    default <T> HResponse<T> badRequest(String message) {
        return error(HStatus.BAD_REQUEST, message);
    }
}
