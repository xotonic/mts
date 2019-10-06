package com.revolut.mts.service;

import com.revolut.mts.dto.Body;
import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.dto.User;
import com.revolut.mts.dto.UserProfile;
import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;

import java.util.Optional;

public interface UsersService {
    HResponse<Body<User>> createUser(RequestContext ctx, String userName) throws Exception;
    HResponse<Body<UserProfile>> getUser(RequestContext ctx, String userName) throws Exception;
    Optional<Integer> checkUserExists(String name) throws  Exception;
    boolean isBalanceSufficient(Integer userId, MoneyAmount minAmount) throws Exception;
}
