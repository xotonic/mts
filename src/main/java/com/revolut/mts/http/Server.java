package com.revolut.mts.http;

import com.revolut.mts.http.routing.Router;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Simple wrapper over {@link HttpServer}.
 * Creates context for {@link Router} and request handlers.
 */
public class Server implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(Server.class);

    private HttpServer server;
    private HttpContext context;

    private Router router;

    /**
     * Build an instance of HTTP server and start it immediately
     * @param router Path router
     * @param port Port to bind
     * @throws IOException
     */
    public Server(Router router, int port) throws IOException {

        this.router = router;

        server = HttpServer.create(new InetSocketAddress(port), 0);
        context = server.createContext("/");
        context.setHandler(this::handleRequest);
        server.start();
        logger.info("Http server started on port {}", port);
    }

    /**
     * Launch request handler in separate thread.
     *
     * @param exchange NIO interface to read request data and write to response
     */
    private void handleRequest(HttpExchange exchange) {
        Executors.newSingleThreadExecutor().submit(() -> processResponse(exchange));
    }

    private void processResponse(HttpExchange exchange) {
        try {
            final var responseProvider = new ResponseProviderImpl();
            var method = HMethod.map(exchange.getRequestMethod());
            logger.info(">>> {}", exchange.getRequestURI());
            var result = router.route(method, exchange.getRequestURI().getPath());
            final var context = new RequestContextImpl(responseProvider, exchange.getRequestBody());
            if (result.exists()) {
                context.setPath(result.getPathValues());
            }
            final var rs = result.getHandler().handle(context).getResponse();
            rs.write(exchange);
        } catch (Exception e) {
            logger.error("Failed to process request", e);
            final var rs = HResponse.createError(HStatus.INTERNAL_ERROR).getResponse();
            try {
                rs.write(exchange);
            } catch (IOException ex) {
                logger.fatal("Failed to send error response", ex);
            }
        } finally {
            logger.info("<<< {}", exchange.getResponseCode());
        }
    }

    /**
     * Stop server. Don not wait for request handlers to finish.
     */
    public void stop() {
        server.stop(0);
    }

    /**
     * Another way to stop this server.
     * Put an instance of this server to try-with-resources block
     * to make it automatically stop after moving out of the scope.
     */
    @Override
    public void close() {
        stop();
    }
}
