package com.glamoraa.service;

import com.glamoraa.dao.CategoryDAO;
import com.glamoraa.dao.OrderDAO;
import com.glamoraa.dao.ProductDAO;
import com.glamoraa.dao.ProductFilter;
import com.glamoraa.dao.ProductImageDAO;
import com.glamoraa.dao.ProductVariantDAO;
import com.glamoraa.dao.ReviewDAO;
import com.glamoraa.dao.impl.CategoryDAOImpl;
import com.glamoraa.dao.impl.OrderDAOImpl;
import com.glamoraa.dao.impl.ProductDAOImpl;
import com.glamoraa.dao.impl.ProductImageDAOImpl;
import com.glamoraa.dao.impl.ProductVariantDAOImpl;
import com.glamoraa.dao.impl.ReviewDAOImpl;
import com.glamoraa.model.Category;
import com.glamoraa.model.Color;
import com.glamoraa.model.Product;
import com.glamoraa.model.ProductVariant;
import com.glamoraa.model.Size;
import com.glamoraa.util.AppConfig;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Page;
import com.glamoraa.util.Tx;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductService {
    private static final long CACHE_MS = 60_000;
    private static volatile List<Category> cachedCategories;
    private static volatile long cachedAt;

    private final ProductDAO products = new ProductDAOImpl();
    private final CategoryDAO categories = new CategoryDAOImpl();
    private final ProductImageDAO images = new ProductImageDAOImpl();
    private final ProductVariantDAO variants = new ProductVariantDAOImpl();
    private final ReviewDAO reviews = new ReviewDAOImpl();
    private final OrderDAO orders = new OrderDAOImpl();

    public static int pageSize() { return Math.max(4, Math.min(48, AppConfig.getInt("catalog.page_size", 12))); }

    /** Active categories for the navigation, cached briefly because every page needs them. */
    public List<Category> navCategories() {
        List<Category> list = cachedCategories;
        if (list == null || System.currentTimeMillis() - cachedAt > CACHE_MS) {
            list = Tx.read(c -> categories.findAll(c, true));
            cachedCategories = list;
            cachedAt = System.currentTimeMillis();
        }
        return list;
    }

    public static void invalidateCategoryCache() { cachedCategories = null; }

    public Category categoryBySlug(String slug) {
        return Tx.read(c -> categories.findBySlug(c, slug));
    }

    /** Paginated, filtered, sorted catalog. Page numbers are clamped so bad input is harmless. */
    public Page<Product> catalog(ProductFilter f, int page) {
        int size = pageSize();
        long total = Tx.read(c -> products.count(c, f));
        int pages = (int) Math.max(1, (total + size - 1) / size);
        int p = Math.max(1, Math.min(page, pages));
        f.limit = size;
        f.offset = (p - 1) * size;
        List<Product> items = Tx.read(c -> products.search(c, f));
        return new Page<>(items, p, size, total);
    }

    public HomeData home() {
        return Tx.read(c -> {
            HomeData h = new HomeData();
            h.setCategories(categories.findAll(c, true));
            ProductFilter nf = new ProductFilter(); nf.newOnly = true; nf.limit = 8;
            h.setNewArrivals(products.search(c, nf));
            ProductFilter ff = new ProductFilter(); ff.featuredOnly = true; ff.limit = 4; ff.sort = "price_desc";
            h.setFeatured(products.search(c, ff));
            h.setBestSellers(products.bestSellers(c, 4));
            return h;
        });
    }

    public ProductDetail detail(long id, long viewerId) {
        return Tx.read(c -> {
            Product p = products.findById(c, id);
            if (p == null || !p.isActive()) throw new NotFoundException("Product not found");
            ProductDetail d = new ProductDetail();
            d.setProduct(p);
            d.setImages(images.findByProduct(c, id));
            List<ProductVariant> vs = variants.findByProduct(c, id, true);
            d.setVariants(vs);
            Map<Long, Size> sizes = new LinkedHashMap<>();
            Map<Long, Color> colors = new LinkedHashMap<>();
            for (ProductVariant v : vs) {
                sizes.computeIfAbsent(v.getSizeId(), k -> { Size s = new Size(); s.setId(k); s.setLabel(v.getSizeLabel()); return s; });
                colors.computeIfAbsent(v.getColorId(), k -> { Color x = new Color(); x.setId(k); x.setName(v.getColorName()); x.setHexCode(v.getColorHex()); return x; });
            }
            d.setSizes(List.copyOf(sizes.values()));
            d.setColors(List.copyOf(colors.values()));
            d.setRelated(products.findRelated(c, p, 4));
            d.setReviews(reviews.findByProduct(c, id, 20));
            d.setCanReview(viewerId > 0 && orders.hasDelivered(c, viewerId, id) && !reviews.exists(c, viewerId, id));
            return d;
        });
    }

    public static BigDecimal parseMoneyOrNull(String s) {
        try {
            BigDecimal b = new BigDecimal(s.trim());
            return b.signum() < 0 ? null : b;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
