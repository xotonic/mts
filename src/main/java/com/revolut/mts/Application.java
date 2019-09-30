package com.revolut.mts;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {

    private final static Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {

        var router = new SimpleRouter();
        router.Get("/status", c -> c.ok(true));

        try (var server = new Server(router, 8080)) {
            logger.info("Server started on {}", server.getListeningPort());
        } catch (Exception e) {
            logger.fatal(e);
        }
    }
}
