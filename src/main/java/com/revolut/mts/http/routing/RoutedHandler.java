package com.revolut.mts.http.routing;

import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;


/**
 * Should handle incoming request and produce response for it.
 */
public interface RoutedHandler {
    /**
     * Handle HTTP request.
     * Can be blocking.
     * @param request Request context
     * @return HTTP response representing success with body or error
     * @throws Exception can be thrown if internal error occurred.
     */
    HResponse handle(RequestContext request) throws Exception;
}
