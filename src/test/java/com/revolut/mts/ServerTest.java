package com.revolut.mts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(HttpClientExtension.class)
class ServerTest {

    @Test
    void serverStartsAndRespondsWithStatus(HttpClient client) throws Exception {
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/status"))
                .build();

        try (var server = new Server()) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONAssert.assertEquals("{\"result\":true,\"error\":null}", response.body(), false);
        }
    }

    @Test
    void serverRespondsWith404OnUnknownPaths(HttpClient client) throws Exception {
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/admin"))
                .build();
        try (var server = new Server()) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }
}