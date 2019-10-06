package com.revolut.mts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserProfile {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("wallet")
    private List<MoneyAmount> wallet;

    public UserProfile() {
    }

    public UserProfile(Integer id, String name, List<MoneyAmount> wallet) {
        this.id = id;
        this.name = name;
        this.wallet = wallet;
    }

    public String getName() {
        return name;
    }

    public List<MoneyAmount> getWallet() {
        return wallet;
    }

    public Integer getId() {
        return id;
    }
}
