package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.EmptyBody;
import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.util.DatabaseExtension;
import com.revolut.mts.util.TestContext;
import fi.iki.elonen.NanoHTTPD;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DatabaseExtension.class)
class MoneyTransferServiceTest {

    @Test
    void depositTest(Database db) throws Exception {
        var usersService = new UsersServiceImpl(db);
        var mtService = new MoneyTransferServiceImpl(db);
        var ctx = new TestContext();
        usersService.createUser(ctx, "tester");

        var rs = mtService.deposit(ctx, "tester",
                new MoneyAmount(new BigDecimal(1.0), Currency.getInstance("USD")));

        assertEquals(NanoHTTPD.Response.Status.OK, rs.getResponse().getStatus());
    }

    @Test
    void depositTestWithNoUser(Database db) throws Exception {
        var mtService = new MoneyTransferServiceImpl(db);
        var ctx = new TestContext();

        var rs = mtService.deposit(ctx, "tester",
                new MoneyAmount(new BigDecimal(1.0), Currency.getInstance("USD")));

        assertEquals(NanoHTTPD.Response.Status.NOT_FOUND, rs.getResponse().getStatus());
    }
}
