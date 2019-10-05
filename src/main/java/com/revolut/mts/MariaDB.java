package com.revolut.mts;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MariaDB implements Database {

    private static final Logger logger = LogManager.getLogger(MariaDB.class);

    protected static final String DB_NAME = "mts";

    protected final DB db;
    protected final MariaDbDataSource datasource;

    public MariaDB() {
        try {
            var config = createConfig();

            logger.info("Installing mysqld");
            db = DB.newEmbeddedDB(config);

            logger.info("Starting the daemon");
            db.start();

            loadSchema();

            datasource = new MariaDbDataSource(config.getURL(DB_NAME));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadSchema() throws ManagedProcessException {
        logger.info("Loading schema");
        db.createDB(DB_NAME);
        db.source("schema.sql", DB_NAME);
    }

    @Override
    public Connection connection() {
        try {
            return datasource.getConnection();
        } catch (SQLException e) {
            logger.error("Failed to get MariaDB connection", e);
            throw new RuntimeException(e);
        }
    }

    private static DBConfiguration createConfig() {
        var config = DBConfigurationBuilder.newBuilder();
        config.setPort(0); // 0 => auto detect free port
        return config.build();
    }
}
