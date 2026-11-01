package com.glamoraa.service;

import com.glamoraa.model.Order;
import com.glamoraa.model.ProductVariant;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardData {
    private BigDecimal revenue;
    private Map<String, Long> ordersByStatus;
    private long totalOrders;
    private long customers;
    private long products;
    private List<ProductVariant> lowStock;
    private List<Order> recentOrders;
    private List<Object[]> topProducts;

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal v) { this.revenue = v; }
    public Map<String, Long> getOrdersByStatus() { return ordersByStatus; }
    public void setOrdersByStatus(Map<String, Long> v) { this.ordersByStatus = v; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long v) { this.totalOrders = v; }
    public long getCustomers() { return customers; }
    public void setCustomers(long v) { this.customers = v; }
    public long getProducts() { return products; }
    public void setProducts(long v) { this.products = v; }
    public List<ProductVariant> getLowStock() { return lowStock; }
    public void setLowStock(List<ProductVariant> v) { this.lowStock = v; }
    public List<Order> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<Order> v) { this.recentOrders = v; }
    public List<Object[]> getTopProducts() { return topProducts; }
    public void setTopProducts(List<Object[]> v) { this.topProducts = v; }
}
