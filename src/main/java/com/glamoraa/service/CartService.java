package com.glamoraa.service;

import com.glamoraa.dao.CartDAO;
import com.glamoraa.dao.ProductVariantDAO;
import com.glamoraa.dao.impl.CartDAOImpl;
import com.glamoraa.dao.impl.ProductVariantDAOImpl;
import com.glamoraa.model.Cart;
import com.glamoraa.model.CartItem;
import com.glamoraa.model.ProductVariant;
import com.glamoraa.util.AppConfig;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.Tx;
import java.sql.Connection;
import java.sql.SQLException;

/** Cart rules. Every method works for the authenticated user id only - ownership is part of every query. */
public class CartService {
    private final CartDAO carts = new CartDAOImpl();
    private final ProductVariantDAO variants = new ProductVariantDAOImpl();

    public static int maxPerItem() { return Math.max(1, AppConfig.getInt("cart.max_quantity_per_item", 10)); }

    private Cart load(Connection c, long userId) throws SQLException {
        Cart cart = carts.findOrCreate(c, userId);
        cart.setItems(carts.items(c, cart.getId()));
        return cart;
    }

    public Cart view(long userId) { return Tx.read(c -> load(c, userId)); }

    public int count(long userId) { return Tx.read(c -> carts.countUnits(c, userId)); }

    public Cart add(long userId, long productId, long variantId, int qty) {
        if (qty < 1 || qty > maxPerItem()) throw new BusinessException("Choose a quantity between 1 and " + maxPerItem() + ".");
        return Tx.write(c -> {
            ProductVariant v = variants.lockById(c, variantId);
            if (v == null || v.getProductId() != productId) throw new BusinessException("That size and colour combination is not available.");
            if (!variants.isPurchasable(c, variantId)) throw new BusinessException("This item is no longer available.");
            if (v.getStockQuantity() < 1) throw new BusinessException("Sorry, this size and colour is out of stock.");
            Cart cart = carts.findOrCreate(c, userId);
            CartItem existing = carts.findItemByVariant(c, cart.getId(), variantId);
            int wanted = (existing == null ? 0 : existing.getQuantity()) + qty;
            if (wanted > maxPerItem()) throw new BusinessException("You can buy up to " + maxPerItem() + " of the same item per order.");
            if (wanted > v.getStockQuantity()) throw new BusinessException("Only " + v.getStockQuantity() + " left in stock for this size and colour.");
            carts.upsertQuantity(c, cart.getId(), variantId, wanted);
            return load(c, userId);
        });
    }

    public Cart updateQuantity(long userId, long itemId, int qty) {
        if (qty < 0 || qty > maxPerItem()) throw new BusinessException("Choose a quantity between 1 and " + maxPerItem() + ".");
        return Tx.write(c -> {
            Cart cart = carts.findOrCreate(c, userId);
            CartItem item = carts.findItem(c, cart.getId(), itemId);     // ownership: the item must be in THIS user's cart
            if (item == null) throw new BusinessException("That item is not in your bag.");
            if (qty == 0) {
                carts.removeItem(c, cart.getId(), itemId);
            } else {
                if (!item.isAvailable()) throw new BusinessException("This item is no longer available. Remove it to continue.");
                if (qty > item.getStock()) throw new BusinessException("Only " + item.getStock() + " left in stock for this size and colour.");
                carts.setQuantity(c, cart.getId(), itemId, qty);
            }
            return load(c, userId);
        });
    }

    public Cart remove(long userId, long itemId) {
        return Tx.write(c -> {
            Cart cart = carts.findOrCreate(c, userId);
            carts.removeItem(c, cart.getId(), itemId);
            return load(c, userId);
        });
    }

    public void clear(long userId) {
        Tx.write(c -> { carts.clear(c, carts.findOrCreate(c, userId).getId()); return null; });
    }
}
