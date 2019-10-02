package com.revolut.mts.util;

import com.revolut.mts.Database;
import com.revolut.mts.H2Database;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.sql.SQLException;

/**
 * Due to process scope of H2 database timeline,
 * we need to clear data from tables before running next test
 */
public class H2DatabaseExtension implements ParameterResolver {

    private Database db = new H2Database();

    private void cleanUsersTable() {
        try {
            final var stmt = db.connection().createStatement();
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("RUNSCRIPT FROM 'classpath:schema.sql'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Database.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        cleanUsersTable();
        return db;
    }
}
