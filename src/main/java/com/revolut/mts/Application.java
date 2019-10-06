package com.revolut.mts;


import com.revolut.mts.dto.Deposit;
import com.revolut.mts.dto.NewTransaction;
import com.revolut.mts.dto.NewUser;
import com.revolut.mts.http.Server;
import com.revolut.mts.http.SimpleRouter;
import com.revolut.mts.service.TxServiceImpl;
import com.revolut.mts.service.UsersServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Application {

    private final static Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        var app = new Application();
        try (var ignored = app.start(new MariaDB())) {
            logger.info("Application started");
        } catch (Exception e) {
            logger.fatal("Failed to run application", e);
        }
    }

    public Server start(Database db) throws IOException {

        var userService = new UsersServiceImpl(db);
        var txService = new TxServiceImpl(db, userService);
        var router = new SimpleRouter();

        router.Post("/users",
                ctx -> userService.createUser(ctx, ctx.body(NewUser.class).getName()));
        router.Get("/users/{string}",
                ctx -> userService.getUser(ctx, ctx.getString(0)));
        router.Post("/deposit",
                ctx -> txService.deposit(ctx, ctx.body(Deposit.class)));
        router.Post("/transactions",
                ctx -> txService.createTransaction(ctx, ctx.body(NewTransaction.class)));
        router.Put("/transactions/{int}",
                ctx -> txService.commitTransaction(ctx, ctx.getInt(0)));
        return new Server(router, 8080);
    }
}
