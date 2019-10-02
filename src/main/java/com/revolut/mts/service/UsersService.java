package com.revolut.mts.service;

import com.revolut.mts.dto.EmptyResponse;
import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.dto.JSONResponse;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    EmptyResponse createUser(String userName) throws Exception;
    JSONResponse<List<MoneyAmount>> getWallet(String userName) throws Exception;

    Optional<Integer> getUser(String userName) throws Exception;
}
