package com.glamoraa.dao.impl;

import com.glamoraa.dao.OrderDAO;
import com.glamoraa.model.Order;
import com.glamoraa.model.OrderItem;
import com.glamoraa.util.Jdbc;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderDAOImpl implements OrderDAO {
    private static final String SELECT =
            "SELECT o.*, u.full_name AS customer_name, u.email AS customer_email, "
            + "(SELECT COALESCE(SUM(oi.quantity), 0) FROM order_items oi WHERE oi.order_id = o.id) AS item_count "
            + "FROM orders o JOIN users u ON u.id = o.user_id ";

    private static Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setOrderNumber(rs.getString("order_number"));
        o.setUserId(rs.getLong("user_id"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setCustomerEmail(rs.getString("customer_email"));
        o.setStatus(rs.getString("status"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setPaymentStatus(rs.getString("payment_status"));
        o.setSubtotal(rs.getBigDecimal("subtotal"));
        o.setShippingFee(rs.getBigDecimal("shipping_fee"));
        o.setTotal(rs.getBigDecimal("total"));
        o.setShipName(rs.getString("ship_name"));
        o.setShipPhone(rs.getString("ship_phone"));
        o.setShipLine1(rs.getString("ship_line1"));
        o.setShipLine2(rs.getString("ship_line2"));
        o.setShipCity(rs.getString("ship_city"));
        o.setShipState(rs.getString("ship_state"));
        o.setShipPostal(rs.getString("ship_postal"));
        o.setShipCountry(rs.getString("ship_country"));
        o.setIdempotencyKey(rs.getString("idempotency_key"));
        o.setStockRestored(rs.getBoolean("stock_restored"));
        o.setPlacedAt(rs.getTimestamp("placed_at"));
        o.setItemCount(rs.getInt("item_count"));
        return o;
    }

    private static OrderItem mapItem(ResultSet rs) throws SQLException {
        OrderItem i = new OrderItem();
        i.setId(rs.getLong("id"));
        i.setOrderId(rs.getLong("order_id"));
        long v = rs.getLong("variant_id"); i.setVariantId(rs.wasNull() ? null : v);
        long p = rs.getLong("product_id"); i.setProductId(rs.wasNull() ? null : p);
        i.setProductName(rs.getString("product_name"));
        i.setSku(rs.getString("sku"));
        i.setSizeLabel(rs.getString("size_label"));
        i.setColorName(rs.getString("color_name"));
        i.setImagePath(rs.getString("image_path"));
        i.setUnitPrice(rs.getBigDecimal("unit_price"));
        i.setQuantity(rs.getInt("quantity"));
        i.setLineTotal(rs.getBigDecimal("line_total"));
        return i;
    }

    @Override public long insert(Connection c, Order o) throws SQLException {
        return Jdbc.insert(c, "INSERT INTO orders (order_number, user_id, status, payment_method, payment_status, subtotal, shipping_fee, total, "
                        + "ship_name, ship_phone, ship_line1, ship_line2, ship_city, ship_state, ship_postal, ship_country, idempotency_key) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                o.getOrderNumber(), o.getUserId(), o.getStatus(), o.getPaymentMethod(), o.getPaymentStatus(), o.getSubtotal(),
                o.getShippingFee(), o.getTotal(), o.getShipName(), o.getShipPhone(), o.getShipLine1(), o.getShipLine2(),
                o.getShipCity(), o.getShipState(), o.getShipPostal(), o.getShipCountry(), o.getIdempotencyKey());
    }

    @Override public void insertItem(Connection c, OrderItem i) throws SQLException {
        Jdbc.insert(c, "INSERT INTO order_items (order_id, variant_id, product_id, product_name, sku, size_label, color_name, image_path, unit_price, quantity, line_total) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                i.getOrderId(), i.getVariantId(), i.getProductId(), i.getProductName(), i.getSku(), i.getSizeLabel(),
                i.getColorName(), i.getImagePath(), i.getUnitPrice(), i.getQuantity(), i.getLineTotal());
    }

    @Override public Order findById(Connection c, long id) throws SQLException {
        return Jdbc.one(c, SELECT + "WHERE o.id = ?", OrderDAOImpl::map, id);
    }

    @Override public Order findForUser(Connection c, long id, long userId) throws SQLException {
        return Jdbc.one(c, SELECT + "WHERE o.id = ? AND o.user_id = ?", OrderDAOImpl::map, id, userId);
    }

    @Override public List<OrderItem> items(Connection c, long orderId) throws SQLException {
        return Jdbc.list(c, "SELECT * FROM order_items WHERE order_id = ? ORDER BY id", OrderDAOImpl::mapItem, orderId);
    }

    @Override public List<Order> findByUser(Connection c, long userId, int limit, int offset) throws SQLException {
        return Jdbc.list(c, SELECT + "WHERE o.user_id = ? ORDER BY o.placed_at DESC, o.id DESC LIMIT ? OFFSET ?", OrderDAOImpl::map, userId, limit, offset);
    }

    @Override public long countByUser(Connection c, long userId) throws SQLException {
        return Jdbc.count(c, "SELECT COUNT(*) FROM orders WHERE user_id = ?", userId);
    }

    private static String like(String q) {
        return "%" + q.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
    }

    private static final String ADMIN_WHERE =
            "WHERE (? = '' OR o.status = ?) AND (o.order_number LIKE ? ESCAPE '!' OR u.full_name LIKE ? ESCAPE '!' OR u.email LIKE ? ESCAPE '!') ";

    @Override public List<Order> adminList(Connection c, String status, String q, int limit, int offset) throws SQLException {
        String l = like(q == null ? "" : q);
        return Jdbc.list(c, SELECT + ADMIN_WHERE + "ORDER BY o.placed_at DESC, o.id DESC LIMIT ? OFFSET ?", OrderDAOImpl::map,
                status, status, l, l, l, limit, offset);
    }

    @Override public long adminCount(Connection c, String status, String q) throws SQLException {
        String l = like(q == null ? "" : q);
        return Jdbc.count(c, "SELECT COUNT(*) FROM orders o JOIN users u ON u.id = o.user_id " + ADMIN_WHERE, status, status, l, l, l);
    }

    @Override public int changeStatus(Connection c, long id, String from, String to) throws SQLException {
        return Jdbc.update(c, "UPDATE orders SET status = ? WHERE id = ? AND status = ?", to, id, from);
    }

    @Override public int markStockRestored(Connection c, long id) throws SQLException {
        return Jdbc.update(c, "UPDATE orders SET stock_restored = 1 WHERE id = ? AND stock_restored = 0", id);
    }

    @Override public void markPaid(Connection c, long id) throws SQLException {
        Jdbc.update(c, "UPDATE orders SET payment_status = 'PAID' WHERE id = ? AND payment_status = 'PENDING'", id);
    }

    @Override public boolean hasDelivered(Connection c, long userId, long productId) throws SQLException {
        return Jdbc.count(c, "SELECT COUNT(*) FROM orders o JOIN order_items oi ON oi.order_id = o.id WHERE o.user_id = ? AND oi.product_id = ? AND o.status = 'DELIVERED'",
                userId, productId) > 0;
    }

    @Override public Map<String, Long> countByStatus(Connection c) throws SQLException {
        Map<String, Long> out = new LinkedHashMap<>();
        for (String s : new String[]{"PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"}) out.put(s, 0L);
        Jdbc.list(c, "SELECT status, COUNT(*) FROM orders GROUP BY status", rs -> { out.put(rs.getString(1), rs.getLong(2)); return 0; });
        return out;
    }

    @Override public BigDecimal revenue(Connection c) throws SQLException {
        BigDecimal v = Jdbc.one(c, "SELECT COALESCE(SUM(total), 0) FROM orders WHERE status <> 'CANCELLED'", rs -> rs.getBigDecimal(1));
        return v == null ? BigDecimal.ZERO : v;
    }

    @Override public List<Object[]> topProducts(Connection c, int limit) throws SQLException {
        return Jdbc.list(c, "SELECT oi.product_name, SUM(oi.quantity) AS units, SUM(oi.line_total) AS revenue FROM order_items oi "
                        + "JOIN orders o ON o.id = oi.order_id WHERE o.status <> 'CANCELLED' GROUP BY oi.product_id, oi.product_name ORDER BY units DESC LIMIT ?",
                rs -> new Object[]{rs.getString(1), rs.getLong(2), rs.getBigDecimal(3)}, limit);
    }
}
