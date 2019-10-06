package com.revolut.mts.service;


import com.revolut.mts.dto.*;
import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;

import java.util.Currency;
import java.util.List;

public interface TxService {

    HResponse<Body<TransactionId>> createTransaction(RequestContext ctx, NewTransaction newTx) throws Exception;

    HResponse<EmptyBody> commitTransaction(RequestContext ctx, Integer id) throws Exception;

    HResponse<EmptyBody> deposit(RequestContext ctx, Deposit deposit) throws Exception;

    HResponse<Body<List<Transaction>>> getTransactions(RequestContext ctx,
                                                          String userName, TransactionQuery query) throws Exception;
}
