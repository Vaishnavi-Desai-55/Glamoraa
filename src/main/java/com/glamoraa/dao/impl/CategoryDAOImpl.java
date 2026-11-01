package com.glamoraa.dao.impl;

import com.glamoraa.dao.CategoryDAO;
import com.glamoraa.model.Category;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {
    private static final String SELECT =
            "SELECT c.id, c.name, c.slug, c.subtitle, c.description, c.image_path, c.sort_order, c.active, "
            + "(SELECT COUNT(*) FROM products p WHERE p.category_id = c.id) AS product_count FROM categories c ";

    private static Category map(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setSlug(rs.getString("slug"));
        c.setSubtitle(rs.getString("subtitle"));
        c.setDescription(rs.getString("description"));
        c.setImagePath(rs.getString("image_path"));
        c.setSortOrder(rs.getInt("sort_order"));
        c.setActive(rs.getBoolean("active"));
        c.setProductCount(rs.getInt("product_count"));
        return c;
    }

    @Override public List<Category> findAll(Connection c, boolean activeOnly) throws SQLException {
        return Jdbc.list(c, SELECT + (activeOnly ? "WHERE c.active = 1 " : "") + "ORDER BY c.sort_order, c.name", CategoryDAOImpl::map);
    }

    @Override public Category findById(Connection c, long id) throws SQLException {
        return Jdbc.one(c, SELECT + "WHERE c.id = ?", CategoryDAOImpl::map, id);
    }

    @Override public Category findBySlug(Connection c, String slug) throws SQLException {
        return Jdbc.one(c, SELECT + "WHERE c.slug = ? AND c.active = 1", CategoryDAOImpl::map, slug);
    }

    @Override public long insert(Connection c, Category x) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO categories (name, slug, subtitle, description, image_path, sort_order, active) VALUES (?,?,?,?,?,?,?)",
                x.getName(), x.getSlug(), x.getSubtitle(), x.getDescription(), x.getImagePath(), x.getSortOrder(), x.isActive());
    }

    @Override public void update(Connection c, Category x) throws SQLException {
        Jdbc.update(c, "UPDATE categories SET name=?, slug=?, subtitle=?, description=?, image_path=?, sort_order=?, active=? WHERE id=?",
                x.getName(), x.getSlug(), x.getSubtitle(), x.getDescription(), x.getImagePath(), x.getSortOrder(), x.isActive(), x.getId());
    }

    @Override public boolean slugExists(Connection c, String slug, long exceptId) throws SQLException {
        return Jdbc.count(c, "SELECT COUNT(*) FROM categories WHERE slug = ? AND id <> ?", slug, exceptId) > 0;
    }

    @Override public int deleteIfEmpty(Connection c, long id) throws SQLException {
        return Jdbc.update(c, "DELETE FROM categories WHERE id = ? AND NOT EXISTS (SELECT 1 FROM products WHERE category_id = ?)", id, id);
    }
}
