package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.Body;
import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.dto.User;
import com.revolut.mts.dto.UserProfile;
import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * A service for working with users in database
 */
public class UsersServiceImpl implements UsersService {

    private Database db;

    public UsersServiceImpl(Database db) {
        this.db = db;
    }

    /**
     * Create user in DB and return its id
     * @param ctx Request context
     * @param userName Unique name of the user
     * @return HTTP response with user id or error
     * @throws SQLException
     */
    @Override
    public HResponse<Body<User>> createUser(RequestContext ctx, String userName) throws SQLException {
        try (var conn = db.connection();
             var stmt = conn.prepareStatement(
                     "INSERT INTO users(name) VALUES (?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, userName);
            stmt.executeUpdate();
            var rs = stmt.getGeneratedKeys();
            rs.next();
            return ctx.created(new Body<>(new User(rs.getInt(1), userName)));
        } catch (SQLIntegrityConstraintViolationException e) {
            return ctx.conflict("The user already exists");
        }
    }

    /**
     * Check if user with given name exists in the database
     * @param userName User name
     * @return Id of the user or nothing if he does not exist
     */
    @Override
    public Optional<Integer> checkUserExists(String userName) throws Exception {
        try (var conn = db.connection();
             var stmt = conn.prepareStatement("SELECT id FROM users WHERE name = ?")) {
            stmt.setString(1, userName);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getInt(1));
            }
            return Optional.empty();
        }
    }

    /**
     * Check whether currency balance of given user is greater than the given
     * @param userId Id of the user holding balance
     * @param minAmount Minimal value of his balance
     * @return True if users has enough balance or false otherwise
     */
    @Override
    public boolean isBalanceSufficient(Integer userId, MoneyAmount minAmount)
            throws SQLException {
        try (var conn = db.connection();
             var stmt = createBalanceCheckStatement(conn, userId, minAmount);
             var rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private PreparedStatement createBalanceCheckStatement(
            Connection connection, Integer userId, MoneyAmount minAmount) throws SQLException {
       var stmt = connection.prepareStatement(
               "SELECT COUNT(*) FROM balances WHERE user_id = ? AND currency = ? AND balance >= ?");
       stmt.setInt(1, userId);
       stmt.setString(2, minAmount.getCurrency());
       stmt.setBigDecimal(3, minAmount.getAmount());
       return stmt;
    }

    /**
     * Read user and theirs balance from database
     * @param ctx Request context
     * @param userName User name
     * @return UserProfile object or error if there is no such user
     */
    @Override
    public HResponse<Body<UserProfile>> getUser(RequestContext ctx, String userName) throws Exception {
        var optId = checkUserExists(userName);
        if (optId.isEmpty()) {
            return ctx.notFound("User not found");
        }
        return getUserWallet(ctx, userName, optId.get());
    }

    private HResponse<Body<UserProfile>> getUserWallet(RequestContext ctx, String userName, Integer id) throws SQLException {
        try (var conn = db.connection();
             var stmt = createWalletStatement(conn, id);
             var rs = stmt.executeQuery()) {
            var list = new ArrayList<MoneyAmount>();
            while (rs.next()) {
                var amount = new MoneyAmount(rs.getBigDecimal(2), rs.getString(1));
                list.add(amount);
            }
            return ctx.ok(new Body<>(new UserProfile(id, userName, list)));
        }
    }

    private PreparedStatement createWalletStatement(Connection conn, Integer userId) throws SQLException {
        var stmt = conn.prepareStatement("SELECT currency, balance FROM balances WHERE user_id = ?");
        stmt.setInt(1, userId);
        return stmt;
    }
}
