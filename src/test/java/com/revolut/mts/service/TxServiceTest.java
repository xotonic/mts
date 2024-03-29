package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.Deposit;
import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.dto.NewTransactionBuilder;
import com.revolut.mts.http.HStatus;
import com.revolut.mts.http.RequestContextImpl;
import com.revolut.mts.http.ResponseProviderImpl;
import com.revolut.mts.util.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DatabaseExtension.class)
class TxServiceTest {

    @Test
    void depositTest(Database db) throws Exception {
        var usersService = new UsersServiceImpl(db);
        var mtService = new TxServiceImpl(db, usersService);
        var ctx = new RequestContextImpl(new ResponseProviderImpl());
        usersService.createUser(ctx, "tester");
        var deposit = new Deposit("tester", new MoneyAmount(new BigDecimal(1.0), "USD"));

        var rs = mtService.deposit(ctx, deposit);

        assertEquals(201, rs.getResponse().getStatus().code());
        final var wallet = usersService.getUser(ctx, "tester").getBody().result().getWallet().get(0);
        assertEquals("USD", wallet.getCurrency());
        assertEquals(new BigDecimal(1.0), wallet.getAmount().stripTrailingZeros());
    }

    @Test
    void depositTestWithNoUser(Database db) throws Exception {
        var mtService = new TxServiceImpl(db, new UsersServiceImpl(db));
        var ctx = new RequestContextImpl(new ResponseProviderImpl());
        var deposit = new Deposit("tester", new MoneyAmount(new BigDecimal(1.0), "USD"));


        var rs = mtService.deposit(ctx, deposit);

        assertEquals(HStatus.NOT_FOUND, rs.getStatus());
    }

    @Test
    void createAndCommitTxHappyPath(Database db) throws Exception {
        final var usersService = new UsersServiceImpl(db);
        var txService = new TxServiceImpl(db, usersService);
        var ctx = new RequestContextImpl(new ResponseProviderImpl());
        usersService.createUser(ctx, "alice");
        usersService.createUser(ctx, "bob");
        var deposit = new Deposit("alice", new MoneyAmount(new BigDecimal(2.0), "USD"));
        txService.deposit(ctx, deposit);

        var tx = new NewTransactionBuilder()
                .setSender("alice")
                .setReceiver("bob")
                .setSourceMoney(1.0, "USD")
                .setTargetCurrency("USD")
                .createNewTransaction();

        var createRs = txService.createTransaction(ctx, tx);
        assertTrue(createRs.isSuccess());
        var commitRs = txService.commitTransaction(ctx, createRs.getBody().result().getId());

        assertTrue(commitRs.isSuccess());
        final var aliceWallet = usersService.getUser(ctx, "alice").getBody().result().getWallet().get(0);
        final var bobWallet = usersService.getUser(ctx, "bob").getBody().result().getWallet().get(0);
        assertEquals(aliceWallet.getCurrency(), bobWallet.getCurrency());
        assertEquals(aliceWallet.getAmount(), bobWallet.getAmount());
    }

}
