package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.Body;
import com.revolut.mts.dto.EmptyBody;
import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.RequestContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersServiceImpl implements UsersService {

    private Database db;

    public UsersServiceImpl(Database db) {
        this.db = db;
    }

    @Override
    public HResponse<EmptyBody> createUser(RequestContext ctx, String userName) throws SQLException {
        try (var conn = db.connection();
             var stmt = conn.prepareStatement("INSERT INTO users(name) VALUES (?)")) {
            stmt.setString(1, userName);

            stmt.executeUpdate();
            return ctx.ok(new EmptyBody());
        } catch (SQLIntegrityConstraintViolationException e) {
            return ctx.conflict("The user already exists");
        }
    }

    @Override
    public Optional<Integer> getUser(String userName) throws Exception {
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
    public HResponse<Body<List<MoneyAmount>>> getWallet(RequestContext ctx,
                                                        String userName) throws Exception {
        try (var conn = db.connection();
             var stmt = createWalletStatement(conn, userName);
             var rs = stmt.executeQuery()) {
            var list = new ArrayList<MoneyAmount>();
            while (rs.next()) {
                var amount = new MoneyAmount(rs.getBigDecimal(1), rs.getString(2));
                list.add(amount);
            }
            return ctx.ok(new Body<>(list));
        }
    }

    private PreparedStatement createWalletStatement(Connection conn, String username) throws SQLException {
        var stmt = conn.prepareStatement("SELECT currency, balance " +
                "FROM balances JOIN users u on balances.user_id = u.id WHERE u.name = ?");
        stmt.setString(1, username);
        return stmt;
    }
}
