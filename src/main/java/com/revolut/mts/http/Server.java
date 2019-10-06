package com.revolut.mts.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revolut.mts.http.routing.Router;
import fi.iki.elonen.NanoHTTPD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Server extends NanoHTTPD implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(Server.class);

    private Router router;
    private ObjectMapper objectMapper;

    public Server(Router router, int port) throws IOException {
        super(port);
        this.router = router;

        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
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
            final var responseProvider = new ResponseProviderImpl();
            var method = HMethod.map(session.getMethod());
            var result = router.route(method, session.getUri());
            final var context = new RequestContextImpl(responseProvider, session.getInputStream());
            if (result.exists()) {
                context.setPath(result.getPathValues());
            }
            return result.getHandler().handle(context).getResponse();
        } catch (Exception e) {
            logger.error("Failed to process request", e);
            return HResponse.createError(HStatus.INTERNAL_ERROR).getResponse();
        }
    }


    @Override
    public void close() {
        stop();
    }

}
