package com.revolut.mts;

interface ResponseProvider {
    HResponse success(HStatus status, Object body);
    HResponse error(HStatus status, String message);
    default HResponse error(HStatus status) {
        return error(status, status.getDescription());
    }
}
