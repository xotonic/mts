package com.revolut.mts;

import com.revolut.mts.http.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouterTest {

    @Test
    void routerDispatchesCorrectly() throws Exception {
        var router = new SimpleRouter();
        router.Get("test", c -> c.ok(true));
        router.Post("test", c -> {
            fail(); return c.error(HStatus.METHOD_NOT_ALLOWED);
        });
        final var handler = router.route(HMethod.GET, "test");
        assertTrue(handler.exists());
        assertEquals(200, handler.getHandler()
                .handle(new RequestContextImpl(new ResponseProviderImpl()))
                .getResponse().getStatus().code()
        );
    }
}