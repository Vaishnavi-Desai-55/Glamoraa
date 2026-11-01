package com.glamoraa.dao.impl;

import com.glamoraa.dao.ProductImageDAO;
import com.glamoraa.model.ProductImage;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductImageDAOImpl implements ProductImageDAO {
    private static ProductImage map(ResultSet rs) throws SQLException {
        ProductImage i = new ProductImage();
        i.setId(rs.getLong("id"));
        i.setProductId(rs.getLong("product_id"));
        i.setImagePath(rs.getString("image_path"));
        i.setAltText(rs.getString("alt_text"));
        i.setSortOrder(rs.getInt("sort_order"));
        i.setPrimary(rs.getBoolean("is_primary"));
        return i;
    }

    @Override public List<ProductImage> findByProduct(Connection c, long productId) throws SQLException {
        return Jdbc.list(c, "SELECT * FROM product_images WHERE product_id = ? ORDER BY is_primary DESC, sort_order, id", ProductImageDAOImpl::map, productId);
    }

    @Override public ProductImage findById(Connection c, long id) throws SQLException {
        return Jdbc.one(c, "SELECT * FROM product_images WHERE id = ?", ProductImageDAOImpl::map, id);
    }

    @Override public long insert(Connection c, ProductImage i) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO product_images (product_id, image_path, alt_text, sort_order, is_primary) VALUES (?,?,?,?,?)",
                i.getProductId(), i.getImagePath(), i.getAltText(), i.getSortOrder(), i.isPrimary());
    }

    @Override public void delete(Connection c, long id) throws SQLException {
        Jdbc.update(c, "DELETE FROM product_images WHERE id = ?", id);
    }

    @Override public void makePrimary(Connection c, long productId, long imageId) throws SQLException {
        Jdbc.update(c, "UPDATE product_images SET is_primary = (id = ?) WHERE product_id = ?", imageId, productId);
    }
}
