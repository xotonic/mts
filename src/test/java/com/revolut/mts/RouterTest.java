package com.revolut.mts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouterTest {

    private static class SuccessException extends RuntimeException {}

    @Test
    void routerDispatchesCorrectly() {
        var router = new SimpleRouter();
        router.Get("test", c -> { throw new SuccessException(); });
        router.Post("test", c -> {
            fail(); return c.error(HStatus.METHOD_NOT_ALLOWED);
        });
        final var handler = router.route(HMethod.GET, "test");
        assertThrows(SuccessException.class, () -> handler.handle(null));

    }
}