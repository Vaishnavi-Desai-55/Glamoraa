package com.glamoraa.dao.impl;

import com.glamoraa.dao.AddressDAO;
import com.glamoraa.model.Address;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AddressDAOImpl implements AddressDAO {
    private static Address map(ResultSet rs) throws SQLException {
        Address a = new Address();
        a.setId(rs.getLong("id"));
        a.setUserId(rs.getLong("user_id"));
        a.setLabel(rs.getString("label"));
        a.setRecipientName(rs.getString("recipient_name"));
        a.setPhone(rs.getString("phone"));
        a.setLine1(rs.getString("line1"));
        a.setLine2(rs.getString("line2"));
        a.setCity(rs.getString("city"));
        a.setState(rs.getString("state"));
        a.setPostalCode(rs.getString("postal_code"));
        a.setCountry(rs.getString("country"));
        a.setDefaultAddress(rs.getBoolean("is_default"));
        return a;
    }

    @Override public List<Address> findByUser(Connection c, long userId) throws SQLException {
        return Jdbc.list(c, "SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC, id", AddressDAOImpl::map, userId);
    }

    /** Ownership is enforced in SQL: the row is only returned for its own user. */
    @Override public Address findForUser(Connection c, long id, long userId) throws SQLException {
        return Jdbc.one(c, "SELECT * FROM addresses WHERE id = ? AND user_id = ?", AddressDAOImpl::map, id, userId);
    }

    @Override public long insert(Connection c, Address a) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO addresses (user_id, label, recipient_name, phone, line1, line2, city, state, postal_code, country, is_default) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                a.getUserId(), a.getLabel(), a.getRecipientName(), a.getPhone(), a.getLine1(), a.getLine2(), a.getCity(),
                a.getState(), a.getPostalCode(), a.getCountry(), a.isDefaultAddress());
    }

    @Override public int update(Connection c, Address a) throws SQLException {
        return Jdbc.update(c, "UPDATE addresses SET label=?, recipient_name=?, phone=?, line1=?, line2=?, city=?, state=?, postal_code=?, country=?, is_default=? WHERE id=? AND user_id=?",
                a.getLabel(), a.getRecipientName(), a.getPhone(), a.getLine1(), a.getLine2(), a.getCity(), a.getState(),
                a.getPostalCode(), a.getCountry(), a.isDefaultAddress(), a.getId(), a.getUserId());
    }

    @Override public int delete(Connection c, long id, long userId) throws SQLException {
        return Jdbc.update(c, "DELETE FROM addresses WHERE id = ? AND user_id = ?", id, userId);
    }

    @Override public void clearDefault(Connection c, long userId) throws SQLException {
        Jdbc.update(c, "UPDATE addresses SET is_default = 0 WHERE user_id = ?", userId);
    }

    @Override public int markDefault(Connection c, long id, long userId) throws SQLException {
        return Jdbc.update(c, "UPDATE addresses SET is_default = 1 WHERE id = ? AND user_id = ?", id, userId);
    }
}
