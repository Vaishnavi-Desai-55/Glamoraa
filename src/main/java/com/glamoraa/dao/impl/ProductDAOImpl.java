package com.glamoraa.dao.impl;

import com.glamoraa.dao.ProductDAO;
import com.glamoraa.dao.ProductFilter;
import com.glamoraa.model.Product;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDAOImpl implements ProductDAO {

    /** One round trip returns the card data too (primary + hover image, rating, stock) - no N+1 queries. */
    static final String BASE_SELECT =
            "SELECT p.id, p.category_id, c.name AS category_name, c.slug AS category_slug, p.name, p.slug, p.description, "
            + "p.specifications, p.price, p.discount_percent, p.sale_price, p.is_featured, p.is_new_arrival, p.active, p.created_at, "
            + "(SELECT i.image_path FROM product_images i WHERE i.product_id = p.id ORDER BY i.is_primary DESC, i.sort_order, i.id LIMIT 1) AS primary_image, "
            + "(SELECT i.image_path FROM product_images i WHERE i.product_id = p.id ORDER BY i.is_primary DESC, i.sort_order, i.id LIMIT 1 OFFSET 1) AS second_image, "
            + "(SELECT AVG(r.rating) FROM reviews r WHERE r.product_id = p.id) AS avg_rating, "
            + "(SELECT COUNT(*) FROM reviews r WHERE r.product_id = p.id) AS review_count, "
            + "(SELECT COALESCE(SUM(v.stock_quantity), 0) FROM product_variants v WHERE v.product_id = p.id AND v.active = 1) AS total_stock "
            + "FROM products p JOIN categories c ON c.id = p.category_id ";

    /** Allowlist: user-supplied sort keys never reach the SQL text. */
    private static final Map<String, String> SORTS = Map.of(
            "newest", "p.created_at DESC, p.id DESC",
            "price_asc", "p.sale_price ASC, p.id",
            "price_desc", "p.sale_price DESC, p.id",
            "name_asc", "p.name ASC, p.id",
            "name_desc", "p.name DESC, p.id");

    static Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setCategorySlug(rs.getString("category_slug"));
        p.setName(rs.getString("name"));
        p.setSlug(rs.getString("slug"));
        p.setDescription(rs.getString("description"));
        p.setSpecifications(rs.getString("specifications"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setDiscountPercent(rs.getInt("discount_percent"));
        p.setSalePrice(rs.getBigDecimal("sale_price"));
        p.setFeatured(rs.getBoolean("is_featured"));
        p.setNewArrival(rs.getBoolean("is_new_arrival"));
        p.setActive(rs.getBoolean("active"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setPrimaryImage(rs.getString("primary_image"));
        p.setSecondImage(rs.getString("second_image"));
        double avg = rs.getDouble("avg_rating");
        p.setAvgRating(rs.wasNull() ? 0 : Math.round(avg * 10) / 10.0);
        p.setReviewCount(rs.getInt("review_count"));
        p.setTotalStock(rs.getInt("total_stock"));
        return p;
    }

    private static String likePattern(String q) {
        return "%" + q.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
    }

    /** Builds the WHERE clause and its bind values from a filter. */
    private static String where(ProductFilter f, List<Object> args) {
        StringBuilder w = new StringBuilder("WHERE 1=1 ");
        if (f.onlyActive) w.append("AND p.active = 1 AND c.active = 1 ");
        if (f.categoryId > 0) { w.append("AND p.category_id = ? "); args.add(f.categoryId); }
        if (f.query != null && !f.query.isBlank()) {
            w.append("AND (p.name LIKE ? ESCAPE '!' OR p.description LIKE ? ESCAPE '!') ");
            String l = likePattern(f.query);
            args.add(l); args.add(l);
        }
        if (f.minPrice != null) { w.append("AND p.sale_price >= ? "); args.add(f.minPrice); }
        if (f.maxPrice != null) { w.append("AND p.sale_price <= ? "); args.add(f.maxPrice); }
        if (f.featuredOnly) w.append("AND p.is_featured = 1 ");
        if (f.newOnly) w.append("AND p.is_new_arrival = 1 ");
        return w.toString();
    }

    @Override public List<Product> search(Connection c, ProductFilter f) throws SQLException {
        List<Object> args = new ArrayList<>();
        String sql = BASE_SELECT + where(f, args) + "ORDER BY " + SORTS.getOrDefault(f.sort, SORTS.get("newest")) + " LIMIT ? OFFSET ?";
        args.add(f.limit);
        args.add(f.offset);
        return Jdbc.list(c, sql, ProductDAOImpl::map, args.toArray());
    }

    @Override public long count(Connection c, ProductFilter f) throws SQLException {
        List<Object> args = new ArrayList<>();
        return Jdbc.count(c, "SELECT COUNT(*) FROM products p JOIN categories c ON c.id = p.category_id " + where(f, args), args.toArray());
    }

    @Override public Product findById(Connection c, long id) throws SQLException {
        return Jdbc.one(c, BASE_SELECT + "WHERE p.id = ?", ProductDAOImpl::map, id);
    }

    @Override public List<Product> findRelated(Connection c, Product p, int limit) throws SQLException {
        return Jdbc.list(c, BASE_SELECT + "WHERE p.category_id = ? AND p.id <> ? AND p.active = 1 AND c.active = 1 ORDER BY p.is_featured DESC, p.id LIMIT ?",
                ProductDAOImpl::map, p.getCategoryId(), p.getId(), limit);
    }

    @Override public List<Product> bestSellers(Connection c, int limit) throws SQLException {
        String sold = "(SELECT COALESCE(SUM(oi.quantity), 0) FROM order_items oi JOIN orders o ON o.id = oi.order_id "
                + "WHERE oi.product_id = p.id AND o.status <> 'CANCELLED')";
        return Jdbc.list(c, BASE_SELECT + "WHERE p.active = 1 AND c.active = 1 AND " + sold + " > 0 ORDER BY " + sold + " DESC LIMIT ?",
                ProductDAOImpl::map, limit);
    }

    @Override public long insert(Connection c, Product p) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO products (category_id, name, slug, description, specifications, price, discount_percent, is_featured, is_new_arrival, active) VALUES (?,?,?,?,?,?,?,?,?,?)",
                p.getCategoryId(), p.getName(), p.getSlug(), p.getDescription(), p.getSpecifications(), p.getPrice(),
                p.getDiscountPercent(), p.isFeatured(), p.isNewArrival(), p.isActive());
    }

    @Override public void update(Connection c, Product p) throws SQLException {
        Jdbc.update(c, "UPDATE products SET category_id=?, name=?, slug=?, description=?, specifications=?, price=?, discount_percent=?, is_featured=?, is_new_arrival=?, active=? WHERE id=?",
                p.getCategoryId(), p.getName(), p.getSlug(), p.getDescription(), p.getSpecifications(), p.getPrice(),
                p.getDiscountPercent(), p.isFeatured(), p.isNewArrival(), p.isActive(), p.getId());
    }

    @Override public void setActive(Connection c, long id, boolean active) throws SQLException {
        Jdbc.update(c, "UPDATE products SET active = ? WHERE id = ?", active, id);
    }

    @Override public boolean slugExists(Connection c, String slug, long exceptId) throws SQLException {
        return Jdbc.count(c, "SELECT COUNT(*) FROM products WHERE slug = ? AND id <> ?", slug, exceptId) > 0;
    }
}
