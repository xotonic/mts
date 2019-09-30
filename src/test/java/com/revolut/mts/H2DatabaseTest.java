package com.revolut.mts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class H2DatabaseTest {

    @Test
    void schemaIsInitializedOnStartUp() throws Exception {
        var db = new H2Database();
        var resultSet = db.connection()
                .createStatement()
                .executeQuery("SELECT * FROM users");
        assertFalse(resultSet.next());
    }
}