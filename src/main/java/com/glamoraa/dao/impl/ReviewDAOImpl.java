package com.glamoraa.dao.impl;

import com.glamoraa.dao.ReviewDAO;
import com.glamoraa.model.Review;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {
    @Override public List<Review> findByProduct(Connection c, long productId, int limit) throws SQLException {
        return Jdbc.list(c, "SELECT r.*, u.full_name FROM reviews r JOIN users u ON u.id = r.user_id WHERE r.product_id = ? ORDER BY r.created_at DESC, r.id DESC LIMIT ?",
                rs -> {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setUserName(rs.getString("full_name"));
                    r.setRating(rs.getInt("rating"));
                    r.setTitle(rs.getString("title"));
                    r.setBody(rs.getString("body"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    return r;
                }, productId, limit);
    }

    @Override public boolean exists(Connection c, long userId, long productId) throws SQLException {
        return Jdbc.count(c, "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ?", userId, productId) > 0;
    }

    @Override public long insert(Connection c, Review r) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO reviews (product_id, user_id, rating, title, body) VALUES (?,?,?,?,?)",
                r.getProductId(), r.getUserId(), r.getRating(), r.getTitle(), r.getBody());
    }
}
