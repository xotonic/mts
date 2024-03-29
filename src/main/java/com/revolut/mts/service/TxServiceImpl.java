package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.*;
import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;
import com.revolut.mts.util.Currencies;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;

/**
 * A service for working with transaction objects
 */
public class TxServiceImpl implements TxService {

    private static final Logger logger = LogManager.getLogger(TxServiceImpl.class);

    private Database db;
    private UsersService usersService;

    /**
     * Construct an instance
     * @param db DB that stores transactions and users
     * @param usersService Service providing access to users
     */
    public TxServiceImpl(Database db, UsersService usersService) {
        this.db = db;
        this.usersService = usersService;
    }

    /**
     * Create transaction in database
     * @param ctx Request context
     * @param tx Data for new transaction
     * @return HTTP Response with tx id or error
     */
    @Override
    public HResponse<Body<TransactionId>> createTransaction(RequestContext ctx, NewTransaction tx) throws Exception {

        var receiverId = usersService.checkUserExists(tx.getReceiver());
        if (receiverId.isEmpty()) {
            return ctx.notFound("Receiver not found");
        }

        var senderId = usersService.checkUserExists(tx.getSender());
        if (senderId.isEmpty()) {
            return ctx.notFound("Sender not found");
        }

        final var srcMoney = tx.getSourceMoney();
        if (!Currencies.validateCurrencies(tx.getTargetCurrency(), srcMoney.getCurrency())) {
            return ctx.notFound("Currency code not found");
        }

        if (!usersService.isBalanceSufficient(senderId.get(), srcMoney)) {
            return ctx.badRequest("Insufficient balance");
        }

        try (var conn = db.connection();
             var stmt = createAndExecuteCreateStatement(conn, tx);
             var rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return ctx.created(new Body<>(new TransactionId(rs.getInt(1))));
            }
            return ctx.badRequest("Sender or/and receiver do not exist");
        }
    }

    private PreparedStatement createAndExecuteCreateStatement(Connection conn, NewTransaction tx)
            throws SQLException {

        var stmt = conn.prepareStatement(
                "INSERT INTO transactions(sender_id, receiver_id, status," +
                "time_created, src_amount, src_currency, dst_currency ) " +
                "SELECT u1.id, u2.id, 'new', NOW(), ?, ?, ? " +
                "FROM users u1 JOIN users u2 ON u1.id != u2.id " +
                "WHERE u1.name = ? OR u2.name = ?", Statement.RETURN_GENERATED_KEYS);
        stmt.setBigDecimal(1, tx.getSourceMoney().getAmount());
        stmt.setString(2, tx.getSourceMoney().getCurrency());
        stmt.setString(3, tx.getTargetCurrency());
        stmt.setString(4, tx.getSender());
        stmt.setString(5, tx.getReceiver());
        stmt.executeUpdate();
        return stmt;
    }

    /**
     * Commit existing transaction.
     * Change atomically balances of users in the transaction
     * @param ctx Request context
     * @param txid Id of existing transaction
     * @return Empty HTTP response
     */
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

    /**
     * Increase balance of user
     * @param ctx Request context
     * @param deposit Input object with username and data about the deposit
     * @return Empty HTTP response or one containing error if check fails
     */
    @Override
    public HResponse<EmptyBody> deposit(RequestContext ctx, Deposit deposit) throws Exception {

        var receiverId = usersService.checkUserExists(deposit.getUserName());
        if (receiverId.isEmpty()) {
            return ctx.notFound("User not found");
        }

        if (deposit.getAmount().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ctx.badRequest("Bad deposit amount");
        }

        try (var conn = db.connection();
             var stmt = createDepositStatement(conn, deposit);
             var ignored = stmt.executeQuery()) {
            return ctx.created(new EmptyBody());
        } catch (SQLIntegrityConstraintViolationException e) {
            return ctx.notFound("The user or currency does not exist");
        }
    }

    private PreparedStatement createDepositStatement(Connection conn, Deposit deposit) throws SQLException {
        final var stmt = conn.prepareStatement(
                "INSERT INTO balances(user_id, currency, balance)" +
                "VALUES ((SELECT id FROM users WHERE name = ?), ?, ?)");
        stmt.setString(1, deposit.getUserName());
        stmt.setString(2, deposit.getAmount().getCurrency());
        stmt.setBigDecimal(3, deposit.getAmount().getAmount());
        return stmt;
    }
}
