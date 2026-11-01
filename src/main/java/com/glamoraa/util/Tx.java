package com.glamoraa.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connection / transaction boundary used by the service layer.
 * read()  : one connection, auto-commit.
 * write() : one connection, ONE transaction - commit on success, rollback on any exception.
 */
public final class Tx {
    private static final Logger LOG = Logger.getLogger(Tx.class.getName());

    private Tx() { }

    public static <T> T read(SqlWork<T> work) {
        try (Connection c = Db.get()) {
            return work.run(c);
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    public static <T> T write(SqlWork<T> work) {
        try (Connection c = Db.get()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                try { c.rollback(); } catch (SQLException rb) { LOG.log(Level.WARNING, "Rollback failed", rb); }
                throw e;
            }
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    private static DataAccessException wrap(SQLException e) {
        LOG.log(Level.SEVERE, "Database error [state=" + e.getSQLState() + ", code=" + e.getErrorCode() + "]", e);
        return new DataAccessException("Database operation failed", e);
    }

    public static boolean isDuplicate(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause())
            if (t instanceof java.sql.SQLIntegrityConstraintViolationException) return true;
        return false;
    }
}
