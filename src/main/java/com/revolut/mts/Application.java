package com.revolut.mts;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Application {

    private final static Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        try (var server = new Server()) {
            logger.info("Server started on {}", server.getListeningPort());
        } catch (Exception e) {
            logger.fatal(e);
        }
    }
}
