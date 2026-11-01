package com.glamoraa.dao;

import java.sql.Connection;
import java.sql.SQLException;

/** Newsletter sign-ups and contact-form messages. */
public interface EngagementDAO {
    /** @return true if newly subscribed, false if the address was already on the list. */
    boolean subscribe(Connection c, String email) throws SQLException;
    void saveContactMessage(Connection c, String name, String email, String subject, String message) throws SQLException;
}
