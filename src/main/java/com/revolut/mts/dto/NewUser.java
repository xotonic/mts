package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewUser {

    @JsonProperty("name")
    private String name;

    public NewUser() {
    }

    public NewUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
