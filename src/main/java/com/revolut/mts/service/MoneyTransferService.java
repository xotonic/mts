package com.revolut.mts.service;


import com.revolut.mts.dto.*;
import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;

import java.util.Currency;
import java.util.List;

public interface MoneyTransferService {

    HResponse<Body<Transaction>> send(RequestContext ctx,
                                      String userName, String recipient, MoneyAmount amount) throws Exception;

    HResponse<Body<Deposit>> deposit(RequestContext ctx,
                                     String userName, MoneyAmount amount) throws Exception;

    HResponse<Body<Transaction>> convertCurrencies(RequestContext ctx,
                                                   String username, MoneyAmount amount, Currency target) throws Exception;

    HResponse<Body<List<Transaction>>> getTransactions(RequestContext ctx,
                                                       String userName, TransactionQuery query) throws Exception;
}
