package com.glamoraa.service;

import com.glamoraa.dao.ProductDAO;
import com.glamoraa.dao.ProductVariantDAO;
import com.glamoraa.dao.WishlistDAO;
import com.glamoraa.dao.impl.ProductDAOImpl;
import com.glamoraa.dao.impl.ProductVariantDAOImpl;
import com.glamoraa.dao.impl.WishlistDAOImpl;
import com.glamoraa.model.Product;
import com.glamoraa.model.ProductVariant;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.Tx;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WishlistService {
    private final WishlistDAO wishlist = new WishlistDAOImpl();
    private final ProductDAO products = new ProductDAOImpl();
    private final ProductVariantDAO variants = new ProductVariantDAOImpl();
    private final CartService cart = new CartService();

    /** Wishlist products plus, for each, the variants that can be bought right now. */
    public static class View {
        public final List<Product> products;
        public final Map<Long, List<ProductVariant>> variantsByProduct;
        View(List<Product> p, Map<Long, List<ProductVariant>> v) { products = p; variantsByProduct = v; }
        public List<Product> getProducts() { return products; }
        public Map<Long, List<ProductVariant>> getVariantsByProduct() { return variantsByProduct; }
    }

    public View view(long userId) {
        return Tx.read(c -> {
            List<Product> list = wishlist.products(c, userId);
            List<Long> ids = new ArrayList<>();
            for (Product p : list) ids.add(p.getId());
            Map<Long, List<ProductVariant>> map = new LinkedHashMap<>();
            for (ProductVariant v : variants.findByProducts(c, ids)) map.computeIfAbsent(v.getProductId(), k -> new ArrayList<>()).add(v);
            return new View(list, map);
        });
    }

    public Set<Long> ids(long userId) { return Tx.read(c -> wishlist.productIds(c, userId)); }

    public void add(long userId, long productId) {
        Tx.write(c -> {
            Product p = products.findById(c, productId);
            if (p == null || !p.isActive()) throw new BusinessException("That product is not available.");
            wishlist.add(c, userId, productId);
            return null;
        });
    }

    public void remove(long userId, long productId) {
        Tx.write(c -> { wishlist.remove(c, userId, productId); return null; });
    }

    /** Toggles and returns true if the product is now wishlisted. */
    public boolean toggle(long userId, long productId) {
        Set<Long> current = new HashSet<>(ids(userId));
        if (current.contains(productId)) { remove(userId, productId); return false; }
        add(userId, productId);
        return true;
    }

    /** Adds the chosen variant to the bag (all cart rules apply) and then drops the product from the wishlist. */
    public void moveToCart(long userId, long productId, long variantId) {
        cart.add(userId, productId, variantId, 1);
        remove(userId, productId);
    }
}
