package com.glamoraa.dao;

import com.glamoraa.model.Review;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ReviewDAO {
    List<Review> findByProduct(Connection c, long productId, int limit) throws SQLException;
    boolean exists(Connection c, long userId, long productId) throws SQLException;
    long insert(Connection c, Review r) throws SQLException;
}
