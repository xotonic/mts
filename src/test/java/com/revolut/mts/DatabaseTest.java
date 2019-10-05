package com.revolut.mts;

import com.revolut.mts.util.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(DatabaseExtension.class)
class DatabaseTest {

    @Test
    void schemaIsInitializedOnStartUp(Database db) throws Exception {
        try (var conn = db.connection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT * FROM users")) {
            assertFalse(rs.next());
        }
    }
}