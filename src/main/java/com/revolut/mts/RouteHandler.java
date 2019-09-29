package com.revolut.mts;

import com.revolut.mts.dto.JSONResponse;

public interface RouteHandler {
    JSONResponse handle(Object request) throws Exception;
}
