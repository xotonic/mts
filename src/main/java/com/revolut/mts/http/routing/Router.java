package com.revolut.mts.http.routing;

import com.revolut.mts.http.HMethod;

/**
 * Holds request handlers and routes incoming requests to them.
 * If search of handler is failed, constructs appropriate response (404, 405)
 */
public interface Router {

    /**
     * Bind given request handler to the path
     * for processing GET requests
     * @param path URL path (e.g. /users/)
     * @param handler The handler to bind
     */
    void Get(String path, RoutedHandler handler);

    /**
     * Bind given request handler to the path
     * for processing POST requests
     * @param path URL path (e.g. /users/)
     * @param handler The handler to bind
     */
    void Post(String path, RoutedHandler handler);

    /**
     * Bind given request handler to the path
     * for processing PUT requests
     * @param path URL path (e.g. /users/)
     * @param handler The handler to bind
     */
    void Put(String path, RoutedHandler handler);

    /**
     * Route the given path with HTTP method to the handler
     * @param method HTTP method to find
     * @param path A path of request. Can contain path arguments
     * @return Found handler or handler constructing appropriate response
     *         in case when the path or method are not found
     */
    RoutingResult route(HMethod method, String path);
}
