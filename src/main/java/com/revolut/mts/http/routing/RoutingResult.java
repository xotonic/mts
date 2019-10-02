package com.revolut.mts.http.routing;

public interface RoutingResult {
    static RoutingResult miss(RoutedHandler handler) {
        return new RoutingResult() {
            @Override
            public RoutedHandler getHandler() {
                return handler;
            }

            @Override
            public boolean exists() {
                return false;
            }

            @Override
            public RoutePath getPathValues() {
                throw new IllegalStateException("This is a not found entry. Only handler can be processed");
            }
        };
    }

    RoutedHandler getHandler();

    boolean exists();

    RoutePath getPathValues();
}
