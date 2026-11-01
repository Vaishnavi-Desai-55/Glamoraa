package com.glamoraa.dao;

import com.glamoraa.model.Address;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AddressDAO {
    List<Address> findByUser(Connection c, long userId) throws SQLException;
    Address findForUser(Connection c, long id, long userId) throws SQLException;
    long insert(Connection c, Address a) throws SQLException;
    int update(Connection c, Address a) throws SQLException;
    int delete(Connection c, long id, long userId) throws SQLException;
    void clearDefault(Connection c, long userId) throws SQLException;
    int markDefault(Connection c, long id, long userId) throws SQLException;
}
