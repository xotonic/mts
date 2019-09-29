package com.revolut.mts.dto;

import java.util.Objects;

/**
 * Represent a prove of identity provided by a user.
 * Order to simplify the API the identity consists
 * only a user name without password.
 */
final public class Credentials {

    private String userName;

    public Credentials(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "userName='" + userName + '\'' +
                '}';
    }
}
