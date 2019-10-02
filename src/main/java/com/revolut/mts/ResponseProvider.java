package com.revolut.mts;

interface ResponseProvider {
    <T> HResponse<T> respond(HStatus status, T body);

    <T> HResponse<T> error(HStatus status, String message);

    default <T> HResponse<T> error(HStatus status) {
        return error(status, status.getDescription());
    }
}
