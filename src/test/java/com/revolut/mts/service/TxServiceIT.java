package com.revolut.mts.service;

import com.revolut.mts.Application;
import com.revolut.mts.Database;
import com.revolut.mts.dto.Deposit;
import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.dto.NewTransactionBuilder;
import com.revolut.mts.dto.NewUser;
import com.revolut.mts.util.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith({DatabaseExtension.class})
class TxServiceIT {

    private void createUser(String user) {
        given().body(new NewUser(user)).when().post("/users").then().statusCode(201);
    }

    private void depositFunds(String user, double amount, String currency) {
        given().body(new Deposit(user, new MoneyAmount(new BigDecimal(amount), currency)))
                .when().post("/deposit")
                .then().statusCode(201);
    }

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
                    .setTargetCurrency("USD")
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

    @Test
    void nonExistingSender(Database db) throws Exception {

        try (var ignored = new Application().start(db)) {
            createUser("bob");
            given().body(new NewTransactionBuilder()
                .setSender("alice")
                .setReceiver("bob")
                .setSourceMoney(2.0, "USD")
                .setTargetCurrency("USD")
                .createNewTransaction())
                .when().post("/transactions")
                .then().statusCode(404);
        }
    }
    @Test
    void nonExistingReceiver(Database db) throws Exception {

        try (var ignored = new Application().start(db)) {
            createUser("alice");
            depositFunds("alice", 4.0, "USD");
            given().body(new NewTransactionBuilder()
                    .setSender("alice")
                    .setReceiver("bob")
                    .setSourceMoney(2.0, "USD")
                    .setTargetCurrency("USD")
                    .createNewTransaction())
                    .when().post("/transactions")
                    .then().statusCode(404);
        }
    }

    @Test
    void wrongCurrencies(Database db) throws Exception {

        try (var ignored = new Application().start(db)) {
            createUser("alice");
            createUser("bob");
            depositFunds("alice", 4.0, "USD");
            given().body(new NewTransactionBuilder()
                    .setSender("alice")
                    .setReceiver("bob")
                    .setSourceMoney(2.0, "RUB") // does not exist
                    .setTargetCurrency("USD")
                    .createNewTransaction())
                    .when().post("/transactions")
                    .then().statusCode(404);
        }
    }

    @Test
    void insufficientBalance(Database db) throws Exception {
        try (var ignored = new Application().start(db)) {
            createUser("alice");
            createUser("bob");
            depositFunds("alice", 1.0, "USD");
            given().body(new NewTransactionBuilder()
                    .setSender("alice")
                    .setReceiver("bob")
                    .setSourceMoney(2.0, "USD")
                    .setTargetCurrency("USD")
                    .createNewTransaction())
                    .when().post("/transactions")
                    .then().statusCode(400);
        }

    }
}
