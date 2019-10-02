package com.revolut.mts.service;

import com.revolut.mts.Database;
import com.revolut.mts.dto.EmptyResponse;
import com.revolut.mts.dto.JSONError;
import com.revolut.mts.dto.JSONResponse;
import com.revolut.mts.dto.MoneyAmount;

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
    public EmptyResponse createUser(String userName) throws SQLException {
        var stmt = db.connection().prepareStatement("INSERT INTO users(name) VALUES (?)");
        stmt.setString(1, userName);
        try {
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            return JSONResponse.failure(new JSONError(409, "The user already exists"));
        }
        return new EmptyResponse();
    }

    @Override
    public Optional<Integer> getUser(String userName) throws Exception {
        var stmt = db.connection().prepareStatement("SELECT id FROM users WHERE name = ?");
        stmt.setString(1, userName);
        var rs = stmt.executeQuery();
        if (rs.next()) {
            return Optional.of(rs.getInt(1));
        }
        return Optional.empty();
    }

    @Override
    public JSONResponse<List<MoneyAmount>> getWallet(String userName) {
        return null;
    }
}
