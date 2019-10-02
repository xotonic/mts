package com.revolut.mts;

import java.util.HashMap;
import java.util.Map;

public class SimpleRouter implements Router {

    private Map<RouteTemplate, Map<HMethod, RoutedHandler>> routes;

    public SimpleRouter() {
        routes = new HashMap<>();
    }

    @Override
    public void Get(String path, RoutedHandler handler) {
        addRoute(HMethod.GET, path, handler);
    }

    @Override
    public void Post(String path, RoutedHandler handler) {
        addRoute(HMethod.POST, path, handler);
    }

    @Override
    public void Put(String path, RoutedHandler handler) {
        addRoute(HMethod.PUT, path, handler);
    }

    private void addRoute(HMethod method, String path, RoutedHandler handler) {
        routes.computeIfAbsent(new RouteTemplate(path), k -> new HashMap<>()).put(method, handler);
    }

    @Override
    public RoutingResult route(HMethod method, String path) {
        var routeEntry = findPathHandlers(path);
        if (routeEntry == null) {
            return RoutingResult.miss(ctx -> ctx.error(HStatus.NOT_FOUND));
         }
        var handler = routeEntry.getValue().get(method);
         if (handler == null) {
             return RoutingResult.miss(ctx -> ctx.error(HStatus.METHOD_NOT_ALLOWED)
                     .withAllowHeader(routeEntry.getValue().keySet()));
         }
        return new RoutingResult() {
            @Override
            public RoutedHandler getHandler() {
                return handler;
            }

            @Override
            public boolean exists() {
                return true;
            }

            @Override
            public RoutePath getPathValues() {
                return new RoutePathImpl(routeEntry.getKey(), path);
            }
        };
    }

    private Map.Entry<RouteTemplate, Map<HMethod, RoutedHandler>> findPathHandlers(String path) {
        return routes.entrySet()
                .stream()
                .filter(e -> e.getKey().isMatched(path))
                .findFirst()
                .orElse(null);
    }
}
