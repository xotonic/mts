package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.http.Server;
import com.revolut.mts.http.SimpleRouter;
import com.revolut.mts.util.DatabaseExtension;
import com.revolut.mts.util.HttpClientExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({HttpClientExtension.class, DatabaseExtension.class})
public class UserServiceIT {

    @Test
    void happyPath(HttpClient client, Database db) throws Exception {
        var service = new UsersServiceImpl(db);
        var router = new SimpleRouter();
        router.Put("/users/{string}", ctx -> service.createUser(ctx, ctx.getString(0)));
        try (var server = new Server(router, 8080)) {
            var request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString("{}"))
                    .uri(URI.create("http://localhost:8080/users/tester"))
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
            final var userId = service.getUser("tester");
            assertTrue(userId.isPresent());
        }
    }
}
