package com.glamoraa.dao;

import com.glamoraa.model.Color;
import com.glamoraa.model.ProductVariant;
import com.glamoraa.model.Size;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProductVariantDAO {
    List<ProductVariant> findByProduct(Connection c, long productId, boolean activeOnly) throws SQLException;
    List<ProductVariant> findByProducts(Connection c, List<Long> productIds) throws SQLException;
    ProductVariant findById(Connection c, long id) throws SQLException;
    long insert(Connection c, ProductVariant v) throws SQLException;
    int updateStock(Connection c, long id, int stock) throws SQLException;
    int setActive(Connection c, long id, boolean active) throws SQLException;
    /** Variant, product and category are all active. */
    boolean isPurchasable(Connection c, long id) throws SQLException;
    /** Locks the row (SELECT ... FOR UPDATE) inside a transaction. */
    ProductVariant lockById(Connection c, long id) throws SQLException;
    /** Conditional decrement - returns 1 only if enough stock was available. */
    int decrementStock(Connection c, long id, int qty) throws SQLException;
    void incrementStock(Connection c, long id, int qty) throws SQLException;
    List<Size> sizes(Connection c) throws SQLException;
    List<Color> colors(Connection c) throws SQLException;
    long insertSize(Connection c, String label, int sortOrder) throws SQLException;
    long insertColor(Connection c, String name, String hex) throws SQLException;
    List<ProductVariant> lowStock(Connection c, int threshold, int limit) throws SQLException;
}
