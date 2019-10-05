package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.Body;
import com.revolut.mts.dto.EmptyBody;
import com.revolut.mts.dto.MoneyAmount;
import com.revolut.mts.http.HResponse;
import com.revolut.mts.http.HStatus;
import com.revolut.mts.http.RequestContext;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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

        } catch (SQLIntegrityConstraintViolationException e) {
            return ctx.error(HStatus.CONFICT, "The user already exists");
        }
        return ctx.ok(new EmptyBody());
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
    public HResponse<Body<List<MoneyAmount>>> getWallet(RequestContext ctx, String userName) {
        return null;
    }
}
