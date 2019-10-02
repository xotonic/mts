package com.revolut.mts.http.routing;

public interface RoutePath {
    String getString(int index);

    int getInt(int index);
}
