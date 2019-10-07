package com.revolut.mts.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.StringJoiner;

/**
 * An HTTP response with JSON body
 * The object can represent ether an response with
 * arbitrary payload or error with message.
 * @param <T> Type of Java object to use as payload
 */
public class HResponse<T> {

    private static final String MIME_TYPE_JSON = "application/json";
    private static ObjectMapper jsonMapper;
    private final HStatus status;
    private final T body;
    private final boolean isError;
    private final String errorMessage;
    private HttpResponse response;

    private HResponse(HStatus status, T body, boolean error, String errorMessage) {
        this.status = status;
        this.body = body;
        isError = error;
        this.errorMessage = errorMessage;

        buildHttpResponse();
    }

    /**
     * Construct the object as a successful response
     * @param status HTTP status
     * @param body Body payload
     * @param <R> Payload type
     * @return New object
     */
    public static <R> HResponse<R> createResponse(HStatus status, R body) {
        return new HResponse<>(status, body, false, null);
    }

    /**
     * Construct the object as an error
     * @param status HTTP status
     * @param message Error message
     * @param <R> Any type
     * @return New object
     */
    public static <R> HResponse<R> createError(HStatus status, String message) {
        return new HResponse<>(status, null, true, message);
    }

    /**
     * Construct the object as an error with default error message
     * based on HTTP code
     * @param status HTTP status
     * @param <R> Any type
     * @return New object
     */
    public static <R> HResponse<R> createError(HStatus status) {
        return new HResponse<>(status, null, true, status.description());
    }

    private static HttpResponse rawError(HStatus status, String message) {
        return new HttpResponse(status, MIME_TYPE_JSON,
                String.format("{\"result\":null,\"error\":{\"code\":%d,\"message\":\"%s\"}}",
                        status.code(), message));
    }

    private static HttpResponse rawError(HStatus code) {
        return rawError(code, code.description());
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

    /**
     * Return a lower level representation of the response
     * @return An object dependent on org.sun.* packages
     */
    public HttpResponse getResponse() {
        return response;
    }

    /**
     * Get body
     * @return Body or null if this object is an error
     */
    public T getBody() {
        return body;
    }

    /**
     * Get HTTP status
     * @return HTTP status
     */
    public HStatus getStatus() {
        return status;
    }

    /**
     * Get error message
     * @return Error message or null if this object is not an error
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Tests whether this instance is an error
     * @return True if this instance is an error
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Tests whether this instance is not an error
     * @return True if this instance is not an error
     */
    public boolean isSuccess() {
        return !isError;
    }

    /**
     * Add "Allow" header
     * @param allowedMethods Collection of methods that are
     *                       allowed to be processed
     * @return This object
     */
    public HResponse withAllowHeader(Iterable<HMethod> allowedMethods) {
        var joiner = new StringJoiner(", ");
        allowedMethods.forEach(m -> joiner.add(m.name()));
        response.addHeader("Allow", joiner.toString());
        return this;
    }

    private HttpResponse response(HStatus code, T body) {
        try {
            initObjectMapper();
            var result = jsonMapper.writeValueAsString(body);
            return new HttpResponse(code, MIME_TYPE_JSON, result);
        } catch (Exception e) {
            return rawError(HStatus.INTERNAL_ERROR);
        }
    }
}
