package com.revolut.mts;

import com.revolut.mts.dto.JSONResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Router {
    private Map<String, RouteHandler> routes;

    public Router() {
        routes = new HashMap<>();
    }
    public void addRoute(String route, RouteHandler handler) {
         routes.put(route, handler);
    }

    public Optional<JSONResponse> route(String route, Object request) throws Exception {
         var handler  = routes.get(route);
         if (handler == null) {
             return Optional.empty();
         }
         return Optional.of(handler.handle(null));
    }

}
