package com.revolut.mts;

import com.revolut.mts.util.classpath.ClassPathURLSupport;
import org.h2.jdbcx.JdbcDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class H2Database {

    private JdbcDataSource dataSource;

    H2Database() {
        // Enable this to make H2 recognize files in resources directory
        ClassPathURLSupport.enable();
    }

    private void initializeDataSource() throws IOException {
        var properties = new Properties();
        properties.load(getClass().getResourceAsStream("/config.properties"));
        dataSource = new JdbcDataSource();
        dataSource.setURL(properties.getProperty("db.url"));
        dataSource.setUser(properties.getProperty("db.username"));
        dataSource.setPassword(properties.getProperty("db.password"));
    }


    public Connection connection() {
        try {
            if (dataSource == null) {
                initializeDataSource();
            }
            return dataSource.getConnection();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
