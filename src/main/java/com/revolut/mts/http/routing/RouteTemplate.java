package com.revolut.mts.http.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Represents a path without known arguments
 * E.g. /users/{id}
 * Can parse string and integer arguments
 * Can be stored as a key
 */
public class RouteTemplate {

    List<Token> tokens;

    public RouteTemplate(String template) {
        var tokenizer = new StringTokenizer(template, "/");

        tokens = new ArrayList<>(tokenizer.countTokens());

        while (tokenizer.hasMoreTokens()) {
            tokens.add(createToken(tokenizer.nextToken()));
        }
    }

    private static Token createToken(String templateToken) {
        if (templateToken.equals("{int}")) {
            return new UIntToken();
        }
        if (templateToken.equals("{string}")) {
            return new StringToken();
        }
        return new DirectoryToken(templateToken);
    }

    public boolean isMatched(String instance) {
        var tokenizer = new StringTokenizer(instance, "/");

        if (tokenizer.countTokens() != tokens.size()) {
            return false;
        }

        var it = tokens.iterator();
        while (tokenizer.hasMoreTokens()) {
            var value = tokenizer.nextToken();
            var token = it.next();
            if (!token.isValid(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteTemplate that = (RouteTemplate) o;
        return tokens.equals(that.tokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokens);
    }

    interface Token {
        boolean isValid(String value);

        default boolean holdsValue() {
            return true;
        }

        default Object getValue(String string) {
            throw new IllegalStateException("This token does not represent any value");
        }
    }

    static class DirectoryToken implements Token {
        private String name;

        DirectoryToken(String name) {
            this.name = name;
        }

        @Override
        public boolean isValid(String value) {
            return name.equals(value);
        }

        @Override
        public boolean holdsValue() {
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DirectoryToken that = (DirectoryToken) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    static class UIntToken implements Token {
        @Override
        public boolean isValid(String value) {
            try {
                Integer.parseUnsignedInt(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public Object getValue(String string) {
            return Integer.parseUnsignedInt(string);
        }
    }

    static class StringToken implements Token {
        @Override
        public boolean isValid(String value) {
            return true;
        }

        @Override
        public Object getValue(String string) {
            return string;
        }

    }
}
