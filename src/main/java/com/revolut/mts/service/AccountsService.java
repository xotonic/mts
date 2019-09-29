package com.revolut.mts.service;

import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.dto.JSONResponse;

import java.util.List;

public interface AccountsService {
    JSONResponse<Void> createUser(String userName);
    JSONResponse<List<MoneyAmount>> getWallet(String userName);
}
