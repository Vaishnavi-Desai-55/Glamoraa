package com.glamoraa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Opens JDBC connections. Kept deliberately simple (DriverManager) so the flow is easy to explain;
 * swap for a Tomcat JNDI DataSource or HikariCP if you need pooling in production.
 */
public final class Db {
    private static final Logger LOG = Logger.getLogger(Db.class.getName());

    static {
        try {
            Class.forName(AppConfig.get("db.driver"));
        } catch (ClassNotFoundException e) {
            LOG.severe("JDBC driver not found: " + AppConfig.get("db.driver"));
        }
    }

    private Db() { }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(AppConfig.get("db.url"), AppConfig.get("db.user"), AppConfig.get("db.password"));
    }
}
