package com.revolut.mts.util;

import com.revolut.mts.Database;
import com.revolut.mts.MariaDB;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Embedded mysqld is starting too long.
 * Add this extension to a test to reuse db instance if it exists.
 *
 * <pre>{@code
 *     @ExtendWith(DatabaseExtension.class)
 *     class TestClass {
 *         @Test
 *         void sampleTest(Database db) { ... }
 *     }
 * }</pre>
 *
 */
public class DatabaseExtension implements ParameterResolver {

    private static ReusableDB db;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Database.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (db == null) {
            db = new ReusableDB();
        } else {
            db.reload();
        }
        return db;
    }

    static class ReusableDB extends MariaDB {
        void reload() {
            try (final var conn = connection();
                 var stmt = conn.createStatement()) {
                stmt.execute("DROP DATABASE " + DB_NAME);
                loadSchema();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
