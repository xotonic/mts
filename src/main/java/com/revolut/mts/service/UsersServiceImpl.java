package com.revolut.mts.service;

import com.revolut.mts.dto.JSONResponse;
import com.revolut.mts.dto.MoneyAmount;

import java.util.List;

public class UsersServiceImpl implements UsersService {
    @Override
    public JSONResponse<Void> createUser(String userName) {
        return null;
    }

    @Override
    public JSONResponse<List<MoneyAmount>> getWallet(String userName) {
        return null;
    }
}
