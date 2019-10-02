package com.revolut.mts;

import com.revolut.mts.http.routing.RoutePathImpl;
import com.revolut.mts.http.routing.RouteTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteParsingTest {
    @Test
    void equality() {
        assertEquals(new RouteTemplate("test"), new RouteTemplate("test"));
    }

    @Test
    void matching() throws Exception {
        assertTrue(new RouteTemplate("/users/{string}").isMatched("/users/admin"));
        assertTrue(new RouteTemplate("/users/{string}").isMatched("/users/admin/"));
        assertTrue(new RouteTemplate("/users/{string}/info").isMatched("/users/admin/info"));
        assertFalse(new RouteTemplate("/users/{string}").isMatched("/cats/admin/"));

        // Static token
        assertTrue(new RouteTemplate("/dir/subdir").isMatched("/dir/subdir"));
        assertTrue(new RouteTemplate("/dir/subdir").isMatched("/dir/subdir/"));
        assertTrue(new RouteTemplate("dir").isMatched("dir"));

        assertTrue(new RouteTemplate("/users/{int}").isMatched("/users/1"));
        assertTrue(new RouteTemplate("/users/{int}/").isMatched("/users/1/"));
        assertFalse(new RouteTemplate("/users/{int}/").isMatched("/users/one"));

        assertTrue(new RouteTemplate("/users/{string}/{int}").isMatched("/users/admin/199"));
    }

    @Test
    void parsing() {
        assertEquals("admin",
                new RoutePathImpl(
                        new RouteTemplate("/users/{string}"),
                        "/users/admin"
                ).getString(0)
        );

        final var twoParams = new RoutePathImpl(
                new RouteTemplate("/users/{string}/{string}"),
                "/users/resource/subresource"
        );
        assertEquals("resource", twoParams.getString(0));
        assertEquals("subresource", twoParams.getString(1));

        assertEquals(392,
                new RoutePathImpl(
                        new RouteTemplate("/users/{int}"),
                        "/users/392"
                ).getInt(0)
        );
    }
}