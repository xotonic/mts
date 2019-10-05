package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.*;
import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Currency;
import java.util.List;

public class MoneyTransferServiceImpl implements MoneyTransferService {

    private Database db;

    public MoneyTransferServiceImpl(Database db) {
        this.db = db;
    }

    @Override
    public HResponse<Body<Transaction>> send(RequestContext ctx,
                                             String userName, String recipient, MoneyAmount amount)
            throws SQLException
    {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public HResponse<EmptyBody> deposit(RequestContext ctx,
                                        String userName, MoneyAmount amount) throws SQLException {
        try (var conn = db.connection();
             var stmt = createDepositStatement(conn, userName, amount);
             var ignored = stmt.executeQuery()
        ) {
            return ctx.ok();
        } catch (SQLIntegrityConstraintViolationException e) {
            return ctx.notFound("The user or currency does not exist");
        }
    }

    private PreparedStatement createDepositStatement(Connection conn,
                                                     String userName, MoneyAmount amount) throws SQLException {
        final var stmt = conn.prepareStatement(
                "INSERT INTO balances(user_id, currency, balance)" +
                "VALUES ((SELECT id FROM users WHERE name = ?), ?, ?)");
        stmt.setString(1, userName);
        stmt.setString(2, amount.getCurrency());
        stmt.setBigDecimal(3, amount.getAmount());
        return stmt;
    }

    @Override
    public HResponse<Body<Transaction>> convertCurrencies(RequestContext ctx,
                                                          String username, MoneyAmount amount, Currency target) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public HResponse<Body<List<Transaction>>> getTransactions(RequestContext ctx,
                                                              String userName, TransactionQuery query) {
        throw new java.lang.UnsupportedOperationException();
    }
}
