package com.glamoraa.dao.impl;

import com.glamoraa.dao.EngagementDAO;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.SQLException;

public class EngagementDAOImpl implements EngagementDAO {
    @Override public boolean subscribe(Connection c, String email) throws SQLException {
        return Jdbc.update(c, "INSERT IGNORE INTO newsletter_subscriptions (email) VALUES (?)", email) > 0;
    }

    @Override public void saveContactMessage(Connection c, String name, String email, String subject, String message) throws SQLException {
        Jdbc.insert(c, "INSERT INTO contact_messages (name, email, subject, message) VALUES (?,?,?,?)", name, email, subject, message);
    }
}
