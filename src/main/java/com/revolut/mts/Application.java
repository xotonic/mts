package com.revolut.mts;


import com.revolut.mts.http.Server;
import com.revolut.mts.http.SimpleRouter;
import com.revolut.mts.service.UsersServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {

    private final static Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {

        var router = new SimpleRouter();
        router.Get("/status", c -> c.ok(true));

        var db = new MariaDB();
        var usersService = new UsersServiceImpl(db);

        router.Put("/users/{}", c -> usersService.createUser(c, c.getString(0)));

        try (var server = new Server(router, 8080)) {
            logger.info("Server started on {}", server.getListeningPort());
        } catch (Exception e) {
            logger.fatal("Failed to run application", e);
        }
    }
}
