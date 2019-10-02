package com.revolut.mts.service;


import com.revolut.mts.dto.*;

import java.util.Currency;
import java.util.List;

public interface MoneyTransferService {
    Body<Transaction> send(String userName, String recipient, MoneyAmount amount);

    Body<Deposit> deposit(String userName, MoneyAmount amount);

    Body<Transaction> convertCurrencies(String username, MoneyAmount amount, Currency target);

    Body<List<Transaction>> getTransactions(String userName, TransactionQuery query);
}
