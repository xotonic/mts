package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

final public class User {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    public User() {}

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }
}
