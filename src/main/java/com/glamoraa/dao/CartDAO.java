package com.glamoraa.dao;

import com.glamoraa.model.Cart;
import com.glamoraa.model.CartItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CartDAO {
    Cart findOrCreate(Connection c, long userId) throws SQLException;
    List<CartItem> items(Connection c, long cartId) throws SQLException;
    CartItem findItem(Connection c, long cartId, long itemId) throws SQLException;
    CartItem findItemByVariant(Connection c, long cartId, long variantId) throws SQLException;
    void upsertQuantity(Connection c, long cartId, long variantId, int quantity) throws SQLException;
    int setQuantity(Connection c, long cartId, long itemId, int quantity) throws SQLException;
    int removeItem(Connection c, long cartId, long itemId) throws SQLException;
    void clear(Connection c, long cartId) throws SQLException;
    int countUnits(Connection c, long userId) throws SQLException;
}
