package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.H2DatabaseExtension;
import com.revolut.mts.dto.EmptyResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(H2DatabaseExtension.class)
class UsersServiceTest {

    @Test
    void happyPath(Database db) throws Exception {
        final var service = new UsersServiceImpl(db);
        assertEquals(new EmptyResponse(), service.createUser("tester"));
        final var userId = service.getUser("tester");
        assertTrue(userId.isPresent());
    }

    @Test
    void createSameSameUser(Database db) throws Exception {
        final var service = new UsersServiceImpl(db);
        assertEquals(new EmptyResponse(), service.createUser("tester"));
        final var rs = service.createUser("tester");
        assertTrue(rs.failed());
        assertEquals(409, rs.error().getCode());
        assertEquals("The user already exists", rs.error().getMessage());
    }

}