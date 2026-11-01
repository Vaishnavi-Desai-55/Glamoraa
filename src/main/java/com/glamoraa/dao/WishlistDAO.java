package com.glamoraa.dao;

import com.glamoraa.model.Product;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface WishlistDAO {
    void add(Connection c, long userId, long productId) throws SQLException;
    int remove(Connection c, long userId, long productId) throws SQLException;
    List<Product> products(Connection c, long userId) throws SQLException;
    Set<Long> productIds(Connection c, long userId) throws SQLException;
}
