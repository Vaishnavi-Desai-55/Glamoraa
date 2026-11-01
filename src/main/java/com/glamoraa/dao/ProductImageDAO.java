package com.glamoraa.dao;

import com.glamoraa.model.ProductImage;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProductImageDAO {
    List<ProductImage> findByProduct(Connection c, long productId) throws SQLException;
    ProductImage findById(Connection c, long id) throws SQLException;
    long insert(Connection c, ProductImage img) throws SQLException;
    void delete(Connection c, long id) throws SQLException;
    void makePrimary(Connection c, long productId, long imageId) throws SQLException;
}
