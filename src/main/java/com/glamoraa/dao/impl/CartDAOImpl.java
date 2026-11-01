package com.glamoraa.dao.impl;

import com.glamoraa.dao.CartDAO;
import com.glamoraa.model.Cart;
import com.glamoraa.model.CartItem;
import com.glamoraa.util.Jdbc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CartDAOImpl implements CartDAO {
    private static final String ITEM_SELECT =
            "SELECT ci.id, ci.cart_id, ci.variant_id, ci.quantity, p.id AS product_id, p.name, s.label AS size_label, "
            + "col.name AS color_name, v.sku, p.sale_price, p.price, v.stock_quantity, "
            + "(SELECT i.image_path FROM product_images i WHERE i.product_id = p.id ORDER BY i.is_primary DESC, i.sort_order, i.id LIMIT 1) AS image_path, "
            + "(v.active = 1 AND p.active = 1 AND c.active = 1) AS available "
            + "FROM cart_items ci JOIN product_variants v ON v.id = ci.variant_id JOIN products p ON p.id = v.product_id "
            + "JOIN categories c ON c.id = p.category_id JOIN sizes s ON s.id = v.size_id JOIN colors col ON col.id = v.color_id ";

    private static CartItem mapItem(ResultSet rs) throws SQLException {
        CartItem i = new CartItem();
        i.setId(rs.getLong("id"));
        i.setCartId(rs.getLong("cart_id"));
        i.setVariantId(rs.getLong("variant_id"));
        i.setQuantity(rs.getInt("quantity"));
        i.setProductId(rs.getLong("product_id"));
        i.setProductName(rs.getString("name"));
        i.setSizeLabel(rs.getString("size_label"));
        i.setColorName(rs.getString("color_name"));
        i.setSku(rs.getString("sku"));
        i.setUnitPrice(rs.getBigDecimal("sale_price"));
        i.setOriginalPrice(rs.getBigDecimal("price"));
        i.setStock(rs.getInt("stock_quantity"));
        i.setImagePath(rs.getString("image_path"));
        i.setAvailable(rs.getBoolean("available"));
        return i;
    }

    private static Cart mapCart(ResultSet rs) throws SQLException {
        Cart c = new Cart();
        c.setId(rs.getLong("id"));
        c.setUserId(rs.getLong("user_id"));
        return c;
    }

    @Override public Cart findOrCreate(Connection c, long userId) throws SQLException {
        Cart cart = Jdbc.one(c, "SELECT id, user_id FROM cart WHERE user_id = ?", CartDAOImpl::mapCart, userId);
        if (cart == null) {
            Jdbc.update(c, "INSERT IGNORE INTO cart (user_id) VALUES (?)", userId);   // IGNORE: safe if two tabs race
            cart = Jdbc.one(c, "SELECT id, user_id FROM cart WHERE user_id = ?", CartDAOImpl::mapCart, userId);
        }
        return cart;
    }

    @Override public List<CartItem> items(Connection c, long cartId) throws SQLException {
        return Jdbc.list(c, ITEM_SELECT + "WHERE ci.cart_id = ? ORDER BY ci.id", CartDAOImpl::mapItem, cartId);
    }

    @Override public CartItem findItem(Connection c, long cartId, long itemId) throws SQLException {
        return Jdbc.one(c, ITEM_SELECT + "WHERE ci.cart_id = ? AND ci.id = ?", CartDAOImpl::mapItem, cartId, itemId);
    }

    @Override public CartItem findItemByVariant(Connection c, long cartId, long variantId) throws SQLException {
        return Jdbc.one(c, ITEM_SELECT + "WHERE ci.cart_id = ? AND ci.variant_id = ?", CartDAOImpl::mapItem, cartId, variantId);
    }

    @Override public void upsertQuantity(Connection c, long cartId, long variantId, int quantity) throws SQLException {
        Jdbc.update(c, "INSERT INTO cart_items (cart_id, variant_id, quantity) VALUES (?,?,?) ON DUPLICATE KEY UPDATE quantity = VALUES(quantity)",
                cartId, variantId, quantity);
    }

    @Override public int setQuantity(Connection c, long cartId, long itemId, int quantity) throws SQLException {
        return Jdbc.update(c, "UPDATE cart_items SET quantity = ? WHERE id = ? AND cart_id = ?", quantity, itemId, cartId);
    }

    @Override public int removeItem(Connection c, long cartId, long itemId) throws SQLException {
        return Jdbc.update(c, "DELETE FROM cart_items WHERE id = ? AND cart_id = ?", itemId, cartId);
    }

    @Override public void clear(Connection c, long cartId) throws SQLException {
        Jdbc.update(c, "DELETE FROM cart_items WHERE cart_id = ?", cartId);
    }

    @Override public int countUnits(Connection c, long userId) throws SQLException {
        return (int) Jdbc.count(c, "SELECT COALESCE(SUM(ci.quantity), 0) FROM cart_items ci JOIN cart ct ON ct.id = ci.cart_id WHERE ct.user_id = ?", userId);
    }
}
