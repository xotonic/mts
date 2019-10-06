package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.http.HStatus;
import com.revolut.mts.http.RequestContextImpl;
import com.revolut.mts.http.ResponseProviderImpl;
import com.revolut.mts.util.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DatabaseExtension.class)
class UsersServiceTest {

    @Test
    void happyPath(Database db) throws Exception {
        final var service = new UsersServiceImpl(db);
        var ctx = new RequestContextImpl(new ResponseProviderImpl());

        var rs = service.createUser(ctx, "tester");
        assertTrue(rs.isSuccess());
        final var userId = service.checkUserExists("tester");
        assertTrue(userId.isPresent());
    }

    @Test
    void walletIsEmptyWhenUserIsNew(Database db) throws Exception {

        final var service = new UsersServiceImpl(db);
        var ctx = new RequestContextImpl(new ResponseProviderImpl());
        service.createUser(ctx, "tester");

        var rs = service.getUser(ctx, "tester");

        assertEquals(HStatus.OK, rs.getStatus());
        assertTrue(rs.getBody().result().getWallet().isEmpty());
    }

    @Test
    void createSameSameUser(Database db) throws Exception {
        final var service = new UsersServiceImpl(db);

        var ctx = new RequestContextImpl(new ResponseProviderImpl());

        var rs = service.createUser(ctx, "tester");
        assertTrue(rs.getBody().succeed());

        rs = service.createUser(ctx, "tester");

        assertFalse(rs.isSuccess());

        assertEquals(HStatus.CONFLICT, rs.getStatus());
        assertEquals("The user already exists", rs.getErrorMessage());
    }
}