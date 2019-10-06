package com.revolut.mts;

import com.revolut.mts.dto.Body;
import com.revolut.mts.dto.User;
import com.revolut.mts.http.Server;
import com.revolut.mts.http.SimpleRouter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class ServerTest {

    @Test
    void serverStartsAndRespondsWithStatus() throws Exception {

        var router = new SimpleRouter();
        router.Get("/status", c -> c.ok(new Body<>(true)));

        try (var ignored = new Server(router, 8080)) {
            when().get("/status")
                    .then()
                    .statusCode(200)
                    .body("result", equalTo(true))
                    .body("error", equalTo(null));
        }

    }

    @Test
    void serverRespondsWith404OnUnknownPaths() throws Exception {
        try (var ignored = new Server(new SimpleRouter(), 8080)) {
            when().get("/status").then().statusCode(404);
        }
    }

    @Test
    void serverRespondsWith405AndAllowHeader() throws Exception {

        var router = new SimpleRouter();
        router.Get("/status", c -> c.ok(new Body<>(true)));

        try (var ignored = new Server(router, 8080)) {
            when()
                    .post("/status")
                    .then()
                    .statusCode(405)
                    .header("allow", equalTo("GET"));
        }
    }

    @Test
    void testRequestBodyDeserialization() throws Exception {

        var router = new SimpleRouter();
        router.Post("/test", c -> c.ok(new Body<>(c.body(User.class))));

        try (var ignored = new Server(router, 8080)) {
            given()
                    .contentType(ContentType.JSON)
                    .body(new User(1, "alice"))
                    .when()
                    .post("/test")
                    .then()
                    .body("result.name", equalTo("alice"))
                    .body("result.id", equalTo(1));
        }
    }

    @Test
    void testInternalErrorHandling() throws Exception {

        var router = new SimpleRouter();
        router.Get("/status", c ->  { throw new RuntimeException(); });

        try (var ignored = new Server(router, 8080)) {
            when().get("/status")
                    .then()
                    .statusCode(500)
                    .body("error.code", equalTo(500))
                    .body("error.message", equalTo("Internal error"))
            ;
        }
    }
}