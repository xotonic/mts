package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

final public class User {

    @JsonProperty("name")
    private String name;

    public User() {}
    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
