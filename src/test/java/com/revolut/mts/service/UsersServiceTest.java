package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.EmptyBody;
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

        assertEquals(new EmptyBody(), rs.getBody());
        final var userId = service.getUser("tester");
        assertTrue(userId.isPresent());
    }

    @Test
    void walletIsEmptyWhenUserIsNew(Database db) throws Exception {

        final var service = new UsersServiceImpl(db);
        var ctx = new RequestContextImpl(new ResponseProviderImpl());
        service.createUser(ctx, "tester");

        var rs = service.getWallet(ctx, "tester");

        assertEquals(HStatus.OK, rs.getStatus());
        assertTrue(rs.getBody().result().isEmpty());
    }

    @Test
    void createSameSameUser(Database db) throws Exception {
        final var service = new UsersServiceImpl(db);

        var ctx = new RequestContextImpl(new ResponseProviderImpl());

        var rs = service.createUser(ctx, "tester");
        assertEquals(new EmptyBody(), rs.getBody());

        rs = service.createUser(ctx, "tester");

        assertFalse(rs.isSuccess());

        assertEquals(HStatus.CONFLICT, rs.getStatus());
        assertEquals("The user already exists", rs.getErrorMessage());
    }
}