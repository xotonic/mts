package com.revolut.mts.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revolut.mts.dto.Body;
import com.revolut.mts.http.routing.Router;
import fi.iki.elonen.NanoHTTPD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Server extends NanoHTTPD implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(Server.class);

    private static String MIME_TYPE_JSON = "application/json";

    private ObjectMapper jsonMapper;
    private Router router;

    public Server(Router router, int port) throws IOException {
        super(port);

        this.router = router;

        jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        logger.info(">>> {}: {}", session.getMethod(), session.getUri());
        final var response = serveInternal(session);
        logger.info("<<< {}", response.getStatus().getRequestStatus());
        return response;
    }

    private Response serveInternal(IHTTPSession session) {
        try {
            final var responseProvider = new ResponseProvider() {
                @Override
                public <T> HResponse<T> respond(HStatus status, T body) {
                    return new HResponse<>(response(status, body));
                }

                @Override
                public <T> HResponse<T> error(HStatus status, String message) {
                    return new HResponse<>(absoluteError(status, message));
                }
            };
            var method = HMethod.map(session.getMethod());
            var result = router.route(method, session.getUri());
            final var context = new RequestContextImpl(responseProvider);
            if (result.exists()) {
                context.setPath(result.getPathValues());
            }
            return result.getHandler().handle(context).getResponse();
        } catch (Exception e) {
            return absoluteError(HStatus.INTERNAL_ERROR);
        }
    }

    private <T> Response response(HStatus code, T body) {
        try {
            var response = new Body<>(body);
            var result = jsonMapper.writeValueAsString(response);
            return newFixedLengthResponse(code.getStatus(), MIME_TYPE_JSON, result);
        } catch (Exception e) {
            return absoluteError(HStatus.INTERNAL_ERROR);
        }
    }

    private Response absoluteError(HStatus code, String message) {
        return newFixedLengthResponse(code.getStatus(), MIME_TYPE_JSON,
                String.format("{\"result\":null,\"error\":{\"code\":%d,\"message:\":\"%s\"}}",
                        code.getRequestStatus(), message));
    }
    private Response absoluteError(HStatus code) {
        return absoluteError(code, code.getDescription());
    }

    @Override
    public void close() {
        stop();
    }
}
