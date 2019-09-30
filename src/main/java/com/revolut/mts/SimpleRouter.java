package com.revolut.mts;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimpleRouter implements Router {

    private Map<String, Map<HMethod, RoutedHandler>> routes;

    public SimpleRouter() {
        routes = new HashMap<>();
    }

    @Override
    public void Get(String route, RoutedHandler handler) {
        routes.getOrDefault(route, new HashMap<>()).put(HMethod.GET, handler);
    }

    @Override
    public void Post(String route, RoutedHandler handler) {
        routes.getOrDefault(route, new HashMap<>()).put(HMethod.GET, handler);
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

    private static class Route {

        private HMethod method;
        private String path;

        public Route(HMethod method, String path) {
            this.method = method;
            this.path = path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Route route = (Route) o;
            return method == route.method &&
                    path.equals(route.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, path);
        }
    }
}
