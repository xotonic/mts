package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.Deposit;
import com.revolut.mts.dto.NewTransaction;
import com.revolut.mts.dto.User;
import com.revolut.mts.http.Server;
import com.revolut.mts.http.SimpleRouter;
import com.revolut.mts.util.DatabaseExtension;
import com.revolut.mts.util.HttpClientExtension;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({HttpClientExtension.class, DatabaseExtension.class})
public class TxServiceIT {

    @Test
    void happyPath(HttpClient client, Database db) throws Exception {
        var userService = new UsersServiceImpl(db);
        var txService = new TxServiceImpl(db, userService);
        var router = new SimpleRouter();

        router.Post("/users",
                ctx -> userService.createUser(ctx, ctx.body(User.class).getName()));
        router.Post("/deposit",
                ctx -> txService.deposit(ctx, ctx.body(Deposit.class)));
        router.Post("/transactions",
                ctx -> txService.createTransaction(ctx, ctx.body(NewTransaction.class)));
        router.Put("/transactions/{int}",
                ctx -> txService.commitTransaction(ctx, ctx.getInt(0)));


        var server = new Server(router, 8080);

        createUser(client, "alice");
        createUser(client, "bob");

        depositFunds(client, "alice", 4.0, "USD");

        var tx = new JSONObject()
                .put("sender", "alice")
                .put("receiver", "bob")
                .put("source_money", amount(2.0, "USD"))
                .put("destination_money", amount(2.0, "USD"));
        var rs = post(client, "/transactions", tx);
        assertEquals(201, rs.statusCode());

        var txid = new JSONObject(rs.body()).getInt("result");

        rs = put(client, "/transactions/" + txid, new JSONObject());
        assertEquals(200, rs.statusCode());
        server.stop();
    }

    private void createUser(HttpClient client, String user) throws Exception {
        var body = new JSONObject();
        body.put("name", user);
        assertEquals(200, post(client, "/users", body).statusCode());
    }

    private void depositFunds(HttpClient client, String user, double amount, String currency) throws Exception {
        var body = new JSONObject()
                .put("username", user)
                .put("amount", amount(amount, currency));
        assertEquals(201, post(client, "/deposit", body).statusCode());
    }

    private static JSONObject amount(double amount, String currency) throws Exception {
        return new JSONObject().put("amount", amount).put("currency", currency);
    }

    private HttpResponse<String> post(HttpClient client, String path, JSONObject body) throws Exception {

        System.out.println(body.toString());
        final var uri = URI.create("http://localhost:8080" + path);
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .uri(uri)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    private HttpResponse<String> put(HttpClient client, String path, JSONObject body) throws Exception {

        final var uri = URI.create("http://localhost:8080" + path);
        var request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .uri(uri)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
