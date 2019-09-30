package com.revolut.mts;

import java.util.HashMap;
import java.util.Map;

public class SimpleRouter implements Router {

    private Map<String, Map<HMethod, RoutedHandler>> routes;

    public SimpleRouter() {
        routes = new HashMap<>();
    }

    @Override
    public void Get(String route, RoutedHandler handler) {
        routes.computeIfAbsent(route, k -> new HashMap<>()).put(HMethod.GET, handler);
    }

    @Override
    public void Post(String route, RoutedHandler handler) {
        routes.computeIfAbsent(route, k -> new HashMap<>()).put(HMethod.POST, handler);
    }

    @Override
    public RoutedHandler route(HMethod method, String path) {
         var registeredPath = routes.get(path);
         if (registeredPath == null) {
             return ctx -> ctx.error(HStatus.NOT_FOUND);
         }
         var handler = registeredPath.get(method);
         if (handler == null) {
             return ctx -> ctx.error(HStatus.METHOD_NOT_ALLOWED)
                     .withAllowHeader(registeredPath.keySet());
         }
         return handler;
    }
}
