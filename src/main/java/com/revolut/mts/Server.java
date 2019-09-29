package com.revolut.mts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revolut.mts.dto.JSONResponse;
import fi.iki.elonen.NanoHTTPD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Server extends NanoHTTPD implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(Server.class);

    private static String MIME_TYPE_JSON = "application/json";

    private ObjectMapper jsonMapper;
    private Router router;

    public Server() throws IOException {
        super(8080);

        jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        router = new Router();
        router.addRoute("/status", rq -> new JSONResponse<>(true));

        start();
    }

    @Override
    public Response serve(IHTTPSession session) {

        logger.info("{}: {}", session.getMethod(), session.getUri());

        try {
            var result = router.route(session.getUri(), session);
            return result
                    .map(this::response)
                    .orElseGet(() -> absoluteError(Response.Status.NOT_FOUND));
        } catch (Exception e) {
            return absoluteError(Response.Status.INTERNAL_ERROR);
        }
    }

    private <T> Response ok(T body) throws JsonProcessingException {
        var response = new JSONResponse<>(body);
        var result = jsonMapper.writeValueAsString(response);
        return newFixedLengthResponse(Response.Status.OK, MIME_TYPE_JSON, result);
    }

    private Response response(JSONResponse response) {

        String result;
        try {
            result = jsonMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return absoluteError(Response.Status.INTERNAL_ERROR);
        }

        if (response.failed()) {
            return newFixedLengthResponse(
                    Response.Status.lookup(response.error().getCode()),
                    MIME_TYPE_JSON, result);
        }

        return newFixedLengthResponse(Response.Status.OK, MIME_TYPE_JSON, result);
    }

    private Response absoluteError(Response.Status code) {
        return newFixedLengthResponse(code, MIME_TYPE_JSON,
                String.format("{\"result\":null,\"error\":{\"code\":%d,\"message:\":\"%s\"}}",
                        code.getRequestStatus(), code.getDescription()));
    }

    @Override
    public void close() {
        stop();
    }
}
