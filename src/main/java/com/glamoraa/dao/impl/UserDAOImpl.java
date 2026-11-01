package com.glamoraa.dao.impl;

import com.glamoraa.dao.UserDAO;
import com.glamoraa.model.User;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private static final String SELECT = "SELECT id, full_name, email, password_hash, phone, role, active, created_at FROM users ";

    static User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        u.setActive(rs.getBoolean("active"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    @Override public User findById(Connection c, long id) throws SQLException {
        return Jdbc.one(c, SELECT + "WHERE id = ?", UserDAOImpl::map, id);
    }

    @Override public User findByEmail(Connection c, String email) throws SQLException {
        return Jdbc.one(c, SELECT + "WHERE email = ?", UserDAOImpl::map, email);
    }

    @Override public boolean emailExists(Connection c, String email) throws SQLException {
        return Jdbc.count(c, "SELECT COUNT(*) FROM users WHERE email = ?", email) > 0;
    }

    @Override public long insert(Connection c, User u) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO users (full_name, email, password_hash, phone, role) VALUES (?,?,?,?,?)",
                u.getFullName(), u.getEmail(), u.getPasswordHash(), u.getPhone(), u.getRole());
    }

    @Override public void updateProfile(Connection c, long id, String fullName, String phone) throws SQLException {
        Jdbc.update(c, "UPDATE users SET full_name = ?, phone = ? WHERE id = ?", fullName, phone, id);
    }

    @Override public void updatePassword(Connection c, long id, String hash) throws SQLException {
        Jdbc.update(c, "UPDATE users SET password_hash = ? WHERE id = ?", hash, id);
    }

    @Override public void setActive(Connection c, long id, boolean active) throws SQLException {
        Jdbc.update(c, "UPDATE users SET active = ? WHERE id = ? AND role = 'CUSTOMER'", active, id);
    }

    @Override public long countAdmins(Connection c) throws SQLException {
        return Jdbc.count(c, "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'");
    }

    private static String like(String q) {
        return "%" + q.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
    }

    @Override public List<User> customers(Connection c, String q, int limit, int offset) throws SQLException {
        String l = like(q == null ? "" : q);
        return Jdbc.list(c, SELECT + "WHERE role = 'CUSTOMER' AND (full_name LIKE ? ESCAPE '!' OR email LIKE ? ESCAPE '!') ORDER BY created_at DESC LIMIT ? OFFSET ?",
                UserDAOImpl::map, l, l, limit, offset);
    }

    @Override public long countCustomers(Connection c, String q) throws SQLException {
        String l = like(q == null ? "" : q);
        return Jdbc.count(c, "SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER' AND (full_name LIKE ? ESCAPE '!' OR email LIKE ? ESCAPE '!')", l, l);
    }
}
