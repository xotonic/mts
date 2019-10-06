package com.revolut.mts.service;

import com.revolut.mts.Application;
import com.revolut.mts.Database;
import com.revolut.mts.dto.*;
import com.revolut.mts.http.Server;
import com.revolut.mts.http.SimpleRouter;
import com.revolut.mts.util.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith({DatabaseExtension.class})
class TxServiceIT {

    @Test
    void happyPath(Database db) throws Exception {

        try (var ignored = new Application().start(db)) {

            createUser("alice");
            createUser("bob");

            depositFunds("alice", 4.0, "USD");

            var txid = given().body(new NewTransactionBuilder()
                    .setSender("alice")
                    .setReceiver("bob")
                    .setSourceMoney(2.0, "USD")
                    .setDestinationMoney(2.0, "USD")
                    .createNewTransaction())
                    .when().post("/transactions")
                    .then().statusCode(201).body("result.id", equalTo(1))
                    .extract().jsonPath().getString("result.id");

            when().put("/transactions/{txid}", txid).then().statusCode(200);

            when().get("/users/alice").then().statusCode(200)
                    .body("result.wallet.find {it.currency == 'USD'}.amount",
                            equalTo(2.0f));
            when().get("/users/bob").then().statusCode(200)
                    .body("result.wallet.find {it.currency == 'USD'}.amount",
                            equalTo(2.0f));
        }
    }

    private void createUser(String user) {
        given().body(new NewUser(user)).when().post("/users").then().statusCode(201);
    }

    private void depositFunds(String user, double amount, String currency) {
        given().body(new Deposit(user, new MoneyAmount(new BigDecimal(amount), currency)))
                .when().post("/deposit")
                .then().statusCode(201);
    }
}
