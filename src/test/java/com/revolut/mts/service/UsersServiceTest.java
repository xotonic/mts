package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.EmptyBody;
import com.revolut.mts.http.HStatus;
import com.revolut.mts.util.DatabaseExtension;
import com.revolut.mts.util.TestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DatabaseExtension.class)
class UsersServiceTest {

    @Test
    void happyPath(Database db) throws Exception {
        final var service = new UsersServiceImpl(db);
        var ctx = new TestContext();
        service.createUser(ctx, "tester");
        assertEquals(new EmptyBody(), ctx.getLastBody(EmptyBody.class));
        final var userId = service.getUser("tester");
        assertTrue(userId.isPresent());
    }

    @Test
    void createSameSameUser(Database db) throws Exception {
        final var service = new UsersServiceImpl(db);

        var ctx = new TestContext();

        service.createUser(ctx, "tester");
        assertEquals(new EmptyBody(), ctx.getLastBody(EmptyBody.class));

        service.createUser(ctx, "tester");

        assertFalse(ctx.isSuccess());

        assertEquals(HStatus.CONFICT, ctx.getLastStatus());
        assertEquals("The user already exists", ctx.getLastErrorMessage());
    }

}