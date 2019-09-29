package com.revolut.mts.service;


import com.revolut.mts.dto.*;

import java.util.Currency;
import java.util.List;

public interface MoneyTransferService {
    JSONResponse<Transaction> send(String userName, String recipient, MoneyAmount amount);
    JSONResponse<Deposit> deposit(String userName, MoneyAmount amount);
    JSONResponse<Transaction> convertCurrencies(String username, MoneyAmount amount, Currency target);
    JSONResponse<List<Transaction>> getTransactions(String userName, TransactionQuery query);
}
