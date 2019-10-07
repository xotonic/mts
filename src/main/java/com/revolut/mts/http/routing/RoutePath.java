package com.revolut.mts.http.routing;

/**
 * Provides API for reading URL path arguments
 */
public interface RoutePath {
    /**
     * Get string by index
     * @param index Index
     * @return String value
     */
    String getString(int index);

    /**
     * Get integer value by index
     * @param index Index
     */
    int getInt(int index);
}
