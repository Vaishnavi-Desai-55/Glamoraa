package com.glamoraa.dao.impl;

import com.glamoraa.dao.WishlistDAO;
import com.glamoraa.model.Product;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WishlistDAOImpl implements WishlistDAO {
    @Override public void add(Connection c, long userId, long productId) throws SQLException {
        Jdbc.update(c, "INSERT IGNORE INTO wishlist (user_id, product_id) VALUES (?,?)", userId, productId);   // unique key blocks duplicates
    }

    @Override public int remove(Connection c, long userId, long productId) throws SQLException {
        return Jdbc.update(c, "DELETE FROM wishlist WHERE user_id = ? AND product_id = ?", userId, productId);
    }

    @Override public List<Product> products(Connection c, long userId) throws SQLException {
        return Jdbc.list(c, ProductDAOImpl.BASE_SELECT + "JOIN wishlist w ON w.product_id = p.id WHERE w.user_id = ? AND p.active = 1 AND c.active = 1 ORDER BY w.created_at DESC, w.id DESC",
                ProductDAOImpl::map, userId);
    }

    @Override public Set<Long> productIds(Connection c, long userId) throws SQLException {
        return new HashSet<>(Jdbc.list(c, "SELECT product_id FROM wishlist WHERE user_id = ?", rs -> rs.getLong(1), userId));
    }
}
