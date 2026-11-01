package com.glamoraa.dao.impl;

import com.glamoraa.dao.ProductVariantDAO;
import com.glamoraa.model.Color;
import com.glamoraa.model.ProductVariant;
import com.glamoraa.model.Size;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductVariantDAOImpl implements ProductVariantDAO {
    private static final String SELECT =
            "SELECT v.id, v.product_id, v.size_id, v.color_id, s.label AS size_label, c.name AS color_name, c.hex_code, "
            + "v.sku, v.stock_quantity, v.active, p.name AS product_name "
            + "FROM product_variants v JOIN sizes s ON s.id = v.size_id JOIN colors c ON c.id = v.color_id JOIN products p ON p.id = v.product_id ";
    private static final String ORDER = " ORDER BY s.sort_order, c.name";

    private static ProductVariant map(ResultSet rs) throws SQLException {
        ProductVariant v = new ProductVariant();
        v.setId(rs.getLong("id"));
        v.setProductId(rs.getLong("product_id"));
        v.setSizeId(rs.getLong("size_id"));
        v.setColorId(rs.getLong("color_id"));
        v.setSizeLabel(rs.getString("size_label"));
        v.setColorName(rs.getString("color_name"));
        v.setColorHex(rs.getString("hex_code"));
        v.setSku(rs.getString("sku"));
        v.setStockQuantity(rs.getInt("stock_quantity"));
        v.setActive(rs.getBoolean("active"));
        v.setProductName(rs.getString("product_name"));
        return v;
    }

    @Override public List<ProductVariant> findByProduct(Connection c, long productId, boolean activeOnly) throws SQLException {
        return Jdbc.list(c, SELECT + "WHERE v.product_id = ? " + (activeOnly ? "AND v.active = 1 " : "") + ORDER, ProductVariantDAOImpl::map, productId);
    }

    @Override public List<ProductVariant> findByProducts(Connection c, List<Long> ids) throws SQLException {
        if (ids.isEmpty()) return new ArrayList<>();
        String marks = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        return Jdbc.list(c, SELECT + "WHERE v.active = 1 AND v.stock_quantity > 0 AND v.product_id IN (" + marks + ")" + ORDER,
                ProductVariantDAOImpl::map, ids.toArray());
    }

    @Override public ProductVariant findById(Connection c, long id) throws SQLException {
        return Jdbc.one(c, SELECT + "WHERE v.id = ?", ProductVariantDAOImpl::map, id);
    }

    @Override public long insert(Connection c, ProductVariant v) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO product_variants (product_id, size_id, color_id, sku, stock_quantity, active) VALUES (?,?,?,?,?,?)",
                v.getProductId(), v.getSizeId(), v.getColorId(), v.getSku(), v.getStockQuantity(), v.isActive());
    }

    @Override public int updateStock(Connection c, long id, int stock) throws SQLException {
        return Jdbc.update(c, "UPDATE product_variants SET stock_quantity = ? WHERE id = ?", stock, id);
    }

    @Override public int setActive(Connection c, long id, boolean active) throws SQLException {
        return Jdbc.update(c, "UPDATE product_variants SET active = ? WHERE id = ?", active, id);
    }

    @Override public boolean isPurchasable(Connection c, long id) throws SQLException {
        return Jdbc.count(c, "SELECT COUNT(*) FROM product_variants v JOIN products p ON p.id = v.product_id JOIN categories g ON g.id = p.category_id "
                + "WHERE v.id = ? AND v.active = 1 AND p.active = 1 AND g.active = 1", id) > 0;
    }

    @Override public ProductVariant lockById(Connection c, long id) throws SQLException {
        Jdbc.one(c, "SELECT id FROM product_variants WHERE id = ? FOR UPDATE", rs -> rs.getLong(1), id);
        return findById(c, id);
    }

    @Override public int decrementStock(Connection c, long id, int qty) throws SQLException {
        return Jdbc.update(c, "UPDATE product_variants SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?", qty, id, qty);
    }

    @Override public void incrementStock(Connection c, long id, int qty) throws SQLException {
        Jdbc.update(c, "UPDATE product_variants SET stock_quantity = stock_quantity + ? WHERE id = ?", qty, id);
    }

    @Override public List<Size> sizes(Connection c) throws SQLException {
        return Jdbc.list(c, "SELECT id, label, sort_order FROM sizes ORDER BY sort_order, label", rs -> {
            Size s = new Size();
            s.setId(rs.getLong(1)); s.setLabel(rs.getString(2)); s.setSortOrder(rs.getInt(3));
            return s;
        });
    }

    @Override public List<Color> colors(Connection c) throws SQLException {
        return Jdbc.list(c, "SELECT id, name, hex_code FROM colors ORDER BY name", rs -> {
            Color x = new Color();
            x.setId(rs.getLong(1)); x.setName(rs.getString(2)); x.setHexCode(rs.getString(3));
            return x;
        });
    }

    @Override public long insertSize(Connection c, String label, int sortOrder) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO sizes (label, sort_order) VALUES (?,?)", label, sortOrder);
    }

    @Override public long insertColor(Connection c, String name, String hex) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO colors (name, hex_code) VALUES (?,?)", name, hex);
    }

    @Override public List<ProductVariant> lowStock(Connection c, int threshold, int limit) throws SQLException {
        return Jdbc.list(c, SELECT + "WHERE v.active = 1 AND p.active = 1 AND v.stock_quantity <= ? ORDER BY v.stock_quantity, p.name LIMIT ?",
                ProductVariantDAOImpl::map, threshold, limit);
    }
}
