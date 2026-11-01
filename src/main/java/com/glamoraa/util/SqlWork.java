package com.glamoraa.util;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlWork<T> {
    T run(Connection c) throws SQLException;
}
