package com.revolut.mts.service;

import com.revolut.mts.HResponse;
import com.revolut.mts.RequestContext;
import com.revolut.mts.dto.Body;
import com.revolut.mts.dto.EmptyBody;
import com.revolut.mts.dto.MoneyAmount;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    HResponse<EmptyBody> createUser(RequestContext ctx, String userName) throws Exception;

    HResponse<Body<List<MoneyAmount>>>
    getWallet(RequestContext ctx, String userName) throws Exception;

    Optional<Integer> getUser(String userName) throws Exception;
}
