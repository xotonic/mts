package com.revolut.mts.http.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RoutePathImpl implements RoutePath {

    private List<Object> values;

    public RoutePathImpl(RouteTemplate template, String instance) throws IllegalArgumentException {
        values = new ArrayList<>();

        var tokenizer = new StringTokenizer(instance, "/");

        for (int i = 0; i < template.tokens.size(); i++) {
            var token = template.tokens.get(i);
            var value = tokenizer.nextToken();
            if (token.holdsValue()) {
                values.add(token.getValue(value));
            }
        }
    }

    @Override
    public String getString(int index) {
        return (String) values.get(index);
    }

    @Override
    public int getInt(int index) {
        return (int) values.get(index);
    }
}
