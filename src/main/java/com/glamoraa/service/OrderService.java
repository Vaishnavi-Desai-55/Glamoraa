package com.glamoraa.service;

import com.glamoraa.dao.OrderDAO;
import com.glamoraa.dao.ProductVariantDAO;
import com.glamoraa.dao.impl.OrderDAOImpl;
import com.glamoraa.dao.impl.ProductVariantDAOImpl;
import com.glamoraa.model.Order;
import com.glamoraa.model.OrderItem;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Page;
import com.glamoraa.util.Tx;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private static final int PAGE = 10;
    private final OrderDAO orders = new OrderDAOImpl();
    private final ProductVariantDAO variants = new ProductVariantDAOImpl();

    public Page<Order> history(long userId, int page) {
        long total = Tx.read(c -> orders.countByUser(c, userId));
        int pages = (int) Math.max(1, (total + PAGE - 1) / PAGE);
        int p = Math.max(1, Math.min(page, pages));
        return new Page<>(Tx.read(c -> orders.findByUser(c, userId, PAGE, (p - 1) * PAGE)), p, PAGE, total);
    }

    /** Only returns the order if it belongs to this user. */
    public Order details(long userId, long orderId) {
        return Tx.read(c -> {
            Order o = orders.findForUser(c, orderId, userId);
            if (o == null) throw new NotFoundException("Order not found");
            o.setItems(orders.items(c, orderId));
            return o;
        });
    }

    public void cancel(long userId, long orderId) {
        Tx.write(c -> {
            Order o = orders.findForUser(c, orderId, userId);
            if (o == null) throw new NotFoundException("Order not found");
            if (!o.isCancellable()) throw new BusinessException("This order can no longer be cancelled because it has been shipped.");
            cancelAndRestock(c, o);
            return null;
        });
    }

    // ----------------------------------------------------------- admin -------
    public Page<Order> adminList(String status, String q, int page) {
        String st = status == null ? "" : status;
        long total = Tx.read(c -> orders.adminCount(c, st, q));
        int pages = (int) Math.max(1, (total + 19) / 20);
        int p = Math.max(1, Math.min(page, pages));
        return new Page<>(Tx.read(c -> orders.adminList(c, st, q, 20, (p - 1) * 20)), p, 20, total);
    }

    public Order adminDetails(long orderId) {
        return Tx.read(c -> {
            Order o = orders.findById(c, orderId);
            if (o == null) throw new NotFoundException("Order not found");
            o.setItems(orders.items(c, orderId));
            return o;
        });
    }

    /** Only the transitions listed in Order.getNextStatuses() are allowed. */
    public void adminChangeStatus(long orderId, String newStatus) {
        Tx.write(c -> {
            Order o = orders.findById(c, orderId);
            if (o == null) throw new NotFoundException("Order not found");
            if (!o.getNextStatuses().contains(newStatus)) throw new BusinessException("An order that is " + o.getStatus().toLowerCase() + " cannot be changed to " + newStatus.toLowerCase() + ".");
            if ("CANCELLED".equals(newStatus)) {
                cancelAndRestock(c, o);
            } else {
                if (orders.changeStatus(c, orderId, o.getStatus(), newStatus) != 1) throw new BusinessException("The order was updated by someone else. Reload and try again.");
                if ("DELIVERED".equals(newStatus)) orders.markPaid(c, orderId);   // cash is collected on delivery
            }
            return null;
        });
    }

    /** Compare-and-set the status, then return stock exactly once (guarded by the stock_restored flag). */
    private void cancelAndRestock(Connection c, Order o) throws SQLException {
        if (orders.changeStatus(c, o.getId(), o.getStatus(), "CANCELLED") != 1) throw new BusinessException("The order was updated by someone else. Reload and try again.");
        if (orders.markStockRestored(c, o.getId()) == 1) {
            List<OrderItem> items = orders.items(c, o.getId());
            for (OrderItem i : items) if (i.getVariantId() != null) variants.incrementStock(c, i.getVariantId(), i.getQuantity());
        }
    }
}
