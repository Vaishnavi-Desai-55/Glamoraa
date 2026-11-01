package com.glamoraa.dao;

import com.glamoraa.model.Order;
import com.glamoraa.model.OrderItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OrderDAO {
    long insert(Connection c, Order o) throws SQLException;
    void insertItem(Connection c, OrderItem i) throws SQLException;
    Order findById(Connection c, long id) throws SQLException;
    Order findForUser(Connection c, long id, long userId) throws SQLException;
    List<OrderItem> items(Connection c, long orderId) throws SQLException;
    List<Order> findByUser(Connection c, long userId, int limit, int offset) throws SQLException;
    long countByUser(Connection c, long userId) throws SQLException;
    List<Order> adminList(Connection c, String status, String q, int limit, int offset) throws SQLException;
    long adminCount(Connection c, String status, String q) throws SQLException;
    /** Compare-and-set status change; returns rows updated (0 if the order was not in the expected state). */
    int changeStatus(Connection c, long id, String from, String to) throws SQLException;
    int markStockRestored(Connection c, long id) throws SQLException;
    void markPaid(Connection c, long id) throws SQLException;
    boolean hasDelivered(Connection c, long userId, long productId) throws SQLException;
    Map<String, Long> countByStatus(Connection c) throws SQLException;
    BigDecimal revenue(Connection c) throws SQLException;
    List<Object[]> topProducts(Connection c, int limit) throws SQLException;
}
