package com.revolut.mts.http;

/**
 * Provides JSON body mapped to Java object
 * Request scoped.
 */
public interface RequestProvider {
    /**
     * Get JSON payload of request
     * Represents the root of the body
     * <pre> {"result": ... } </pre>
     * @param clazz A class to map the JSON object
     * @param <T> A type to map the JSON object
     * @return New instance of T mapped from JSON object
     */
    <T> T body(Class<T> clazz);
}
