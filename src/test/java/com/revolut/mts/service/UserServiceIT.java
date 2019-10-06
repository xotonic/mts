package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.NewUser;
import com.revolut.mts.http.Server;
import com.revolut.mts.http.SimpleRouter;
import com.revolut.mts.util.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({DatabaseExtension.class})
class UserServiceIT {

    @Test
    void happyPath(Database db) throws Exception {

        var service = new UsersServiceImpl(db);
        var router = new SimpleRouter();
        router.Post("/users", ctx -> service.createUser(ctx, ctx.body(NewUser.class).getName()));
        router.Get("/users/{string}", ctx -> service.getUser(ctx, ctx.getString(0)));

        try (var ignored = new Server(router, 8080)) {

            var id = given().body(new NewUser("tester"))
                    .when().post("/users")
                    .then().statusCode(201)
                    .extract().jsonPath().getInt("result.id");

            when().get("/users/tester")
                    .then().statusCode(200)
                    .body("result.id", equalTo(id))
                    .body("result.name", equalTo("tester"))
                    .body("result.wallet", hasSize(equalTo(0)));
        }
    }

    @Test
    void respond404IfUsedDoesNotExist(Database db) throws Exception {

        var service = new UsersServiceImpl(db);
        var router = new SimpleRouter();
        router.Get("/users/{string}", ctx -> service.getUser(ctx, ctx.getString(0)));

        try (var ignored = new Server(router, 8080)) {
            when().get("/users/tester").then().statusCode(404);
        }
    }
}
