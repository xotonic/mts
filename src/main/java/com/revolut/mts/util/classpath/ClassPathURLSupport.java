package com.revolut.mts.util.classpath;

import java.net.URL;

final public class ClassPathURLSupport {

    public static void enable() {

        if (enabled) {
            return;
        }

        final var urlHandler = new ClassPathURLStreamHandler();
        final var handlerFactory = new ConfigurableStreamHandlerFactory("classpath", urlHandler);
        URL.setURLStreamHandlerFactory(handlerFactory);
        enabled = true;
    }

    private static boolean enabled;

    private ClassPathURLSupport() {}
}
