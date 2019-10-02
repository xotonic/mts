package com.revolut.mts;

public interface Router {
    void Get(String path, RoutedHandler handler);

    void Post(String path, RoutedHandler handler);

    void Put(String path, RoutedHandler handler);

    RoutingResult route(HMethod method, String path);
}
