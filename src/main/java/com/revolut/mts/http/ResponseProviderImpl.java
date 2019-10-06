package com.revolut.mts.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ResponseProviderImpl implements ResponseProvider {


    @Override
    public <T> HResponse<T> respond(HStatus status, T body) {
        return HResponse.createResponse(status, body);
    }

    @Override
    public <T> HResponse<T> error(HStatus status, String message) {
        return HResponse.createError(status, message);
    }

}
