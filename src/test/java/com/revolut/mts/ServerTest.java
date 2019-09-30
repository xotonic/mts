package com.revolut.mts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(HttpClientExtension.class)
class ServerTest {

    @Test
    void serverStartsAndRespondsWithStatus(HttpClient client) throws Exception {
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/status"))
                .build();

        var router = new SimpleRouter();
        router.Get("/status", c -> c.ok(true));

        try (var server = new Server(router, 8080)) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONAssert.assertEquals(
                    "{\"result\":true,\"error\":null}",
                    response.body(), false);
        }
    }

    @Test
    void serverRespondsWith404OnUnknownPaths(HttpClient client) throws Exception {
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/status"))
                .build();
        try (var server = new Server(new SimpleRouter(), 8080)) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    void serverRespondsWith405AndAllowHeader(HttpClient client) throws Exception {
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .uri(URI.create("http://localhost:8080/status"))
                .build();

        var router = new SimpleRouter();
        router.Get("/status", c -> c.ok(true));

        try (var server = new Server(router, 8080)) {
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final var allowed = response.headers().firstValue("allow");

            assertEquals(405, response.statusCode());
            allowed.ifPresentOrElse(v -> assertEquals("GET", v), Assertions::fail);
        }
    }
}