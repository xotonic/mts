package com.revolut.mts;

public interface RoutedHandler {
    HResponse handle(RequestContext request) throws Exception;
}
