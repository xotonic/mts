package com.revolut.mts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(H2DatabaseExtension.class)
class H2DatabaseTest {

    @Test
    void schemaIsInitializedOnStartUp(Database db) throws Exception {
        var resultSet = db.connection()
                .createStatement()
                .executeQuery("SELECT * FROM users");
        assertFalse(resultSet.next());
    }
}