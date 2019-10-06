package com.revolut.mts.http;

public interface RequestProvider {
    <T> T body(Class<T> clazz);
}
