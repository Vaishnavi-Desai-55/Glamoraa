package com.glamoraa.dao;

import com.glamoraa.model.Product;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    List<Product> search(Connection c, ProductFilter f) throws SQLException;
    long count(Connection c, ProductFilter f) throws SQLException;
    Product findById(Connection c, long id) throws SQLException;
    List<Product> findRelated(Connection c, Product p, int limit) throws SQLException;
    List<Product> bestSellers(Connection c, int limit) throws SQLException;
    long insert(Connection c, Product p) throws SQLException;
    void update(Connection c, Product p) throws SQLException;
    void setActive(Connection c, long id, boolean active) throws SQLException;
    boolean slugExists(Connection c, String slug, long exceptId) throws SQLException;
}
