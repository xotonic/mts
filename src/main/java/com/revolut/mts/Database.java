package com.revolut.mts;

import java.sql.Connection;

/**
 * Basically, we only need a JDBC connection
 */
public interface Database {
    Connection connection();
}
