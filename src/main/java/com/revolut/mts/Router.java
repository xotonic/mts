package com.revolut.mts;

public interface Router {
    void Get(String route, RoutedHandler handler);
    void Post(String route, RoutedHandler handler);

    RoutedHandler route(HMethod method, String route);
}
