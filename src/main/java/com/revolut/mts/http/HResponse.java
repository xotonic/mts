package com.revolut.mts.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revolut.mts.dto.Body;
import fi.iki.elonen.NanoHTTPD;

import java.util.StringJoiner;

public class HResponse<T> {

    private static final String MIME_TYPE_JSON = "application/json";
    private static ObjectMapper jsonMapper;
    private final HStatus status;
    private final T body;
    private final boolean isError;
    private final String errorMessage;
    private NanoHTTPD.Response response;

    private HResponse(HStatus status, T body, boolean error, String errorMessage) {
        this.status = status;
        this.body = body;
        isError = error;
        this.errorMessage = errorMessage;

        buildHttpResponse();
    }

    public static <R> HResponse<R> createResponse(HStatus status, R body) {
        return new HResponse<>(status, body, false, null);
    }

    public static <R> HResponse<R> createError(HStatus status, String message) {
        return new HResponse<>(status, null, true, message);
    }

    public static <R> HResponse<R> createError(HStatus status) {
        return new HResponse<>(status, null, true, status.getDescription());
    }

    private static NanoHTTPD.Response rawError(HStatus code, String message) {
        return NanoHTTPD.newFixedLengthResponse(code.getStatus(), MIME_TYPE_JSON,
                String.format("{\"result\":null,\"error\":{\"code\":%d,\"message:\":\"%s\"}}",
                        code.getRequestStatus(), message));
    }

    private static NanoHTTPD.Response rawError(HStatus code) {
        return rawError(code, code.getDescription());
    }

    private void buildHttpResponse() {
        if (this.isError) {
            response = rawError(this.status, this.errorMessage);
        } else {
            response = response(this.status, this.body);
        }
    }

    private void initObjectMapper() {
        if (jsonMapper == null) {
            jsonMapper = new ObjectMapper();
            jsonMapper.registerModule(new JavaTimeModule());
        }
    }

    public NanoHTTPD.Response getResponse() {
        return response;
    }

    public T getBody() {
        return body;
    }

    public HStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isError() {
        return isError;
    }
    public boolean isSuccess() {
        return !isError;
    }

    public HResponse withAllowHeader(Iterable<HMethod> allowedMethods) {
        var joiner = new StringJoiner(", ");
        allowedMethods.forEach(m -> joiner.add(m.name()));
        response.addHeader("Allow", joiner.toString());
        return this;
    }

    private NanoHTTPD.Response response(HStatus code, T body) {
        try {
            var response = new Body<>(body);
            initObjectMapper();
            var result = jsonMapper.writeValueAsString(response);
            return NanoHTTPD.newFixedLengthResponse(code.getStatus(), MIME_TYPE_JSON, result);
        } catch (Exception e) {
            return rawError(HStatus.INTERNAL_ERROR);
        }
    }
}
