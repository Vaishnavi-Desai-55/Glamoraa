package com.glamoraa.dao;

import com.glamoraa.model.Category;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CategoryDAO {
    List<Category> findAll(Connection c, boolean activeOnly) throws SQLException;
    Category findById(Connection c, long id) throws SQLException;
    Category findBySlug(Connection c, String slug) throws SQLException;
    long insert(Connection c, Category cat) throws SQLException;
    void update(Connection c, Category cat) throws SQLException;
    boolean slugExists(Connection c, String slug, long exceptId) throws SQLException;
    int deleteIfEmpty(Connection c, long id) throws SQLException;
}
