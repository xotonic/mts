package com.revolut.mts.http.routing;

import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;

public interface RoutedHandler {
    HResponse handle(RequestContext request) throws Exception;
}
