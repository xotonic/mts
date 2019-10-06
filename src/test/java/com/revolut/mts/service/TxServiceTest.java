package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.http.RequestContextImpl;
import com.revolut.mts.http.ResponseProviderImpl;
import com.revolut.mts.util.DatabaseExtension;
import fi.iki.elonen.NanoHTTPD;
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
        var mtService = new TxServiceImpl(db);
        var ctx = new RequestContextImpl(new ResponseProviderImpl());
        usersService.createUser(ctx, "tester");

        var rs = mtService.deposit(ctx, "tester",
                new MoneyAmount(new BigDecimal(1.0), "USD"));

        assertEquals(NanoHTTPD.Response.Status.OK, rs.getResponse().getStatus());
    }

    @Test
    void depositTestWithNoUser(Database db) throws Exception {
        var mtService = new TxServiceImpl(db);
        var ctx = new RequestContextImpl(new ResponseProviderImpl());

        var rs = mtService.deposit(ctx, "tester",
                new MoneyAmount(new BigDecimal(1.0), "USD"));

        assertEquals(NanoHTTPD.Response.Status.NOT_FOUND, rs.getResponse().getStatus());
    }

}
