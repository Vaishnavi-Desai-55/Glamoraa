package com.glamoraa.dao;

import com.glamoraa.model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    User findById(Connection c, long id) throws SQLException;
    User findByEmail(Connection c, String email) throws SQLException;
    boolean emailExists(Connection c, String email) throws SQLException;
    long insert(Connection c, User u) throws SQLException;
    void updateProfile(Connection c, long id, String fullName, String phone) throws SQLException;
    void updatePassword(Connection c, long id, String hash) throws SQLException;
    void setActive(Connection c, long id, boolean active) throws SQLException;
    long countAdmins(Connection c) throws SQLException;
    List<User> customers(Connection c, String q, int limit, int offset) throws SQLException;
    long countCustomers(Connection c, String q) throws SQLException;
}
