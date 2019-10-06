package com.revolut.mts;

import com.revolut.mts.dto.Body;
import com.revolut.mts.dto.EmptyBody;
import com.revolut.mts.dto.User;
import com.revolut.mts.http.Server;
import com.revolut.mts.http.SimpleRouter;
import com.revolut.mts.util.HttpClientExtension;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

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

        var server = new Server(router, 8080);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONAssert.assertEquals(
                "{\"result\":true,\"error\":null}",
                response.body(), false);
        server.stop();

    }

    @Test
    void serverRespondsWith404OnUnknownPaths(HttpClient client) throws Exception {
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/status"))
                .build();
        var server = new Server(new SimpleRouter(), 8080);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        server.stop();
    }

    @Test
    void serverRespondsWith405AndAllowHeader(HttpClient client) throws Exception {
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .uri(URI.create("http://localhost:8080/status"))
                .build();

        var router = new SimpleRouter();
        router.Get("/status", c -> c.ok(true));

        var server = new Server(router, 8080);
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        final var allowed = response.headers().firstValue("allow");

        assertEquals(405, response.statusCode());
        allowed.ifPresentOrElse(v -> assertEquals("GET", v), Assertions::fail);
        server.stop();
    }

    @Test
    void requestBody(HttpClient client) throws Exception {

        var requestBody = new JSONObject().put("name", "alice");

        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .uri(URI.create("http://localhost:8080/test"))
                .build();

        var router = new SimpleRouter();
        router.Post("/test", c -> c.ok(c.body(User.class)));

        var server = new Server(router, 8080);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var expected = new JSONObject().put("result", requestBody).put("result", null);
        JSONAssert.assertEquals(expected.toString(), response.body(), false);
        server.stop();
    }

    @Test
    void requestBody1(HttpClient client) throws Exception {

        var router = new SimpleRouter();
        router.Post("/test", c -> c.ok(new Body<>(c.body(User.class))));

        var server = new Server(router, 8080);

        given()
            .contentType(ContentType.JSON)
            .body(new User("alice"))
        .when()
            .post("/test")
        .then()
            .body("result", equalTo(5));
    }
}