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

public class UsersServiceImpl implements UsersService {

    private Database db;

    public UsersServiceImpl(Database db) {
        this.db = db;
    }

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
