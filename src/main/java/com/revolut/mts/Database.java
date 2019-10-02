package com.revolut.mts;

import java.sql.Connection;

public interface Database {
    Connection connection();
}
