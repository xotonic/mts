package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.*;
import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;

import java.sql.*;
import java.util.Currency;
import java.util.List;

public class TxServiceImpl implements TxService {

    private Database db;

    public TxServiceImpl(Database db) {
        this.db = db;
    }

    @Override
    public HResponse<Integer> createTransaction(RequestContext ctx, NewTransaction tx) throws Exception {
        try (var conn = db.connection();
             var stmt = createAndExecuteCreateStatement(conn, tx);
             var rs = stmt.getGeneratedKeys()) {
            return ctx.created(rs.getInt(1));
        }
    }

    private PreparedStatement createAndExecuteCreateStatement(Connection conn, NewTransaction tx)
            throws SQLException {

        var stmt = conn.prepareStatement(
                "INSERT INTO transactions(sender_id, receiver_id, status," +
                "time_created, src_amount, src_currency, dst_amount, dst_currency ) " +
                "SELECT u1.id, u2.id, 'new', NOW(), ?, ?, ?, ? " +
                "FROM users u1 JOIN users u2 ON u1.id != u2.id " +
                "WHERE u1.name = ? OR u2.name = ?", Statement.RETURN_GENERATED_KEYS);
        stmt.setBigDecimal(1, tx.getSourceMoney().getAmount());
        stmt.setString(2, tx.getSourceMoney().getCurrency());
        stmt.setBigDecimal(3, tx.getDestinationMoney().getAmount());
        stmt.setString(4, tx.getDestinationMoney().getCurrency());
        stmt.setString(5, tx.getSender());
        stmt.setString(6, tx.getReceiver());
        stmt.executeUpdate();
        return stmt;
    }

    @Override
    public HResponse<EmptyBody> commitTransaction(RequestContext ctx,  Integer txid) throws SQLException
    {
        try (var conn = db.connection();
            var stmt = createCommitStatement(conn, txid))
        {
            stmt.execute();
            return ctx.ok();
        }
    }

    private CallableStatement createCommitStatement(Connection conn, Integer txid) throws SQLException {
        var stmt = conn.prepareCall("{CALL commit_tx(?)}");
        stmt.setInt(1, txid);
        return stmt;
    }

    @Override
    public HResponse<EmptyBody> deposit(RequestContext ctx,
                                        String userName, MoneyAmount amount) throws SQLException {
        try (var conn = db.connection();
             var stmt = createDepositStatement(conn, userName, amount);
             var ignored = stmt.executeQuery()) {
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
    public HResponse<Body<List<Transaction>>> getTransactions(RequestContext ctx,
                                                                 String userName, TransactionQuery query) {
        throw new java.lang.UnsupportedOperationException();
    }
}
