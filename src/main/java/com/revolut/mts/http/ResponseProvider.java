package com.revolut.mts.http;

/**
 * Constructs a JSON response from given HTTP status and serializable POJO
 */
interface ResponseProvider {

    /**
     * Construct an object representing a successful response
     * @param status HTTP status of the response
     * @param body Response payload
     * @param <T> Type of the payload
     * @return HTTP Response
     */
    <T> HResponse<T> respond(HStatus status, T body);

    /**
     * Construct a response representing error
     * @param status HTTP status of the response
     * @param message A simple string describing the error for user
     * @param <T> Any type
     * @return HTTP Response
     */
    <T> HResponse<T> error(HStatus status, String message);

    /**
     * Construct a response representing error
     * @param status HTTP status of the response
     * @param <T> Any type
     * @return HTTP Response
     */
    default <T> HResponse<T> error(HStatus status) {
        return error(status, status.description());
    }
}
