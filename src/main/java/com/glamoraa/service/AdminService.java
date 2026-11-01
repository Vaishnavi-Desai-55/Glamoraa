package com.glamoraa.service;

import com.glamoraa.dao.CategoryDAO;
import com.glamoraa.dao.OrderDAO;
import com.glamoraa.dao.ProductDAO;
import com.glamoraa.dao.ProductFilter;
import com.glamoraa.dao.ProductImageDAO;
import com.glamoraa.dao.ProductVariantDAO;
import com.glamoraa.dao.UserDAO;
import com.glamoraa.dao.impl.CategoryDAOImpl;
import com.glamoraa.dao.impl.OrderDAOImpl;
import com.glamoraa.dao.impl.ProductDAOImpl;
import com.glamoraa.dao.impl.ProductImageDAOImpl;
import com.glamoraa.dao.impl.ProductVariantDAOImpl;
import com.glamoraa.dao.impl.UserDAOImpl;
import com.glamoraa.model.Category;
import com.glamoraa.model.Color;
import com.glamoraa.model.Product;
import com.glamoraa.model.ProductImage;
import com.glamoraa.model.ProductVariant;
import com.glamoraa.model.Size;
import com.glamoraa.model.User;
import com.glamoraa.util.AppConfig;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.FormException;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Page;
import com.glamoraa.util.Slugs;
import com.glamoraa.util.Tx;
import com.glamoraa.util.Validator;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/** Back-office operations. Every call is reached only through AdminFilter (server-side role check). */
public class AdminService {
    private final ProductDAO products = new ProductDAOImpl();
    private final CategoryDAO categories = new CategoryDAOImpl();
    private final ProductImageDAO images = new ProductImageDAOImpl();
    private final ProductVariantDAO variants = new ProductVariantDAOImpl();
    private final OrderDAO orders = new OrderDAOImpl();
    private final UserDAO users = new UserDAOImpl();

    // ---------------------------------------------------------- dashboard ----
    public DashboardData dashboard() {
        return Tx.read(c -> {
            DashboardData d = new DashboardData();
            d.setRevenue(orders.revenue(c));
            d.setOrdersByStatus(orders.countByStatus(c));
            d.setTotalOrders(d.getOrdersByStatus().values().stream().mapToLong(Long::longValue).sum());
            d.setCustomers(users.countCustomers(c, ""));
            ProductFilter all = new ProductFilter(); all.onlyActive = false;
            d.setProducts(products.count(c, all));
            d.setLowStock(variants.lowStock(c, AppConfig.getInt("low_stock.threshold", 5), 10));
            d.setRecentOrders(orders.adminList(c, "", "", 6, 0));
            d.setTopProducts(orders.topProducts(c, 5));
            return d;
        });
    }

    // ----------------------------------------------------------- products ----
    public Page<Product> products(String q, long categoryId, int page) {
        ProductFilter f = new ProductFilter();
        f.onlyActive = false; f.query = q; f.categoryId = categoryId; f.sort = "newest";
        int size = 15;
        long total = Tx.read(c -> products.count(c, f));
        int pages = (int) Math.max(1, (total + size - 1) / size);
        int p = Math.max(1, Math.min(page, pages));
        f.limit = size; f.offset = (p - 1) * size;
        return new Page<>(Tx.read(c -> products.search(c, f)), p, size, total);
    }

    public Product product(long id) {
        Product p = Tx.read(c -> products.findById(c, id));
        if (p == null) throw new NotFoundException("Product not found");
        return p;
    }

    public List<Category> allCategories() { return Tx.read(c -> categories.findAll(c, false)); }

    /** Insert when p.id == 0, otherwise update. Price and discount are validated here, never trusted from the browser. */
    public long saveProduct(Product p, String priceText) {
        p.setName(Validator.clean(p.getName()));
        p.setDescription(Validator.clean(p.getDescription()));
        p.setSpecifications(Validator.clean(p.getSpecifications()));
        Validator v = new Validator().required("name", p.getName(), "Name", 160).optional("description", p.getDescription(), "Description", 5000)
                .optional("specifications", p.getSpecifications(), "Specifications", 3000)
                .money("price", priceText, "Price").range("discountPercent", p.getDiscountPercent(), 0, 90, "Discount");
        if (p.getCategoryId() <= 0) v.error("categoryId", "Choose a category.");
        if (!v.ok()) throw new FormException(v.errors());
        p.setPrice(new BigDecimal(priceText).setScale(2, java.math.RoundingMode.HALF_UP));
        return Tx.write(c -> {
            if (categories.findById(c, p.getCategoryId()) == null) throw new FormException(new Validator().error("categoryId", "Choose a valid category.").errors());
            p.setSlug(uniqueSlug(c, Slugs.of(p.getName()), p.getId(), true));
            if (p.getId() == 0) { p.setId(products.insert(c, p)); } else { products.update(c, p); }
            return p.getId();
        });
    }

    public void setProductActive(long id, boolean active) { Tx.write(c -> { products.setActive(c, id, active); return null; }); }

    private String uniqueSlug(Connection c, String base, long exceptId, boolean product) throws SQLException {
        String slug = base;
        for (int n = 2; (product ? products.slugExists(c, slug, exceptId) : categories.slugExists(c, slug, exceptId)); n++) slug = base + "-" + n;
        return slug;
    }

    // ---------------------------------------------------------- categories ---
    public long saveCategory(Category cat) {
        cat.setName(Validator.clean(cat.getName()));
        cat.setSubtitle(Validator.clean(cat.getSubtitle()));
        cat.setDescription(Validator.clean(cat.getDescription()));
        cat.setImagePath(Validator.clean(cat.getImagePath()));
        Validator v = new Validator().required("name", cat.getName(), "Name", 80).optional("subtitle", cat.getSubtitle(), "Subtitle", 160)
                .optional("description", cat.getDescription(), "Description", 2000).optional("imagePath", cat.getImagePath(), "Image path", 255);
        if (cat.getImagePath().startsWith("http") || cat.getImagePath().contains("..")) v.error("imagePath", "Use a path inside the site, such as /assets/images/x.svg.");
        if (!v.ok()) throw new FormException(v.errors());
        long id = Tx.write(c -> {
            cat.setSlug(uniqueSlug(c, Slugs.of(cat.getName()), cat.getId(), false));
            try {
                if (cat.getId() == 0) cat.setId(categories.insert(c, cat)); else categories.update(c, cat);
            } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                throw new FormException(new Validator().error("name", "A category with this name already exists.").errors());
            }
            return cat.getId();
        });
        ProductService.invalidateCategoryCache();
        return id;
    }

    public void deleteCategory(long id) {
        int n = Tx.write(c -> categories.deleteIfEmpty(c, id));
        if (n == 0) throw new BusinessException("A category that still contains products cannot be deleted. Deactivate it instead.");
        ProductService.invalidateCategoryCache();
    }

    // -------------------------------------------------------------- images ---
    public List<ProductImage> images(long productId) { return Tx.read(c -> images.findByProduct(c, productId)); }

    public void addImage(long productId, String path, String alt) {
        Tx.write(c -> {
            if (products.findById(c, productId) == null) throw new NotFoundException("Product not found");
            List<ProductImage> existing = images.findByProduct(c, productId);
            ProductImage i = new ProductImage();
            i.setProductId(productId); i.setImagePath(path); i.setAltText(alt);
            i.setSortOrder(existing.size()); i.setPrimary(existing.isEmpty());
            images.insert(c, i);
            return null;
        });
    }

    /** Returns the removed image's path so the caller can delete an uploaded file. */
    public String deleteImage(long productId, long imageId) {
        return Tx.write(c -> {
            ProductImage i = images.findById(c, imageId);
            if (i == null || i.getProductId() != productId) throw new NotFoundException("Image not found");
            images.delete(c, imageId);
            List<ProductImage> rest = images.findByProduct(c, productId);
            if (i.isPrimary() && !rest.isEmpty()) images.makePrimary(c, productId, rest.get(0).getId());
            return i.getImagePath();
        });
    }

    public void makePrimary(long productId, long imageId) {
        Tx.write(c -> {
            ProductImage i = images.findById(c, imageId);
            if (i == null || i.getProductId() != productId) throw new NotFoundException("Image not found");
            images.makePrimary(c, productId, imageId);
            return null;
        });
    }

    // ------------------------------------------------------------ variants ---
    public List<ProductVariant> variants(long productId) { return Tx.read(c -> variants.findByProduct(c, productId, false)); }
    public List<Size> sizes() { return Tx.read(variants::sizes); }
    public List<Color> colors() { return Tx.read(variants::colors); }

    public void addVariant(long productId, long sizeId, long colorId, int stock) {
        if (stock < 0 || stock > 100000) throw new BusinessException("Stock must be between 0 and 100000.");
        Tx.write(c -> {
            Product p = products.findById(c, productId);
            if (p == null) throw new NotFoundException("Product not found");
            Size size = variants.sizes(c).stream().filter(s -> s.getId() == sizeId).findFirst().orElseThrow(() -> new BusinessException("Choose a size."));
            Color color = variants.colors(c).stream().filter(x -> x.getId() == colorId).findFirst().orElseThrow(() -> new BusinessException("Choose a colour."));
            ProductVariant v = new ProductVariant();
            v.setProductId(productId); v.setSizeId(sizeId); v.setColorId(colorId); v.setStockQuantity(stock); v.setActive(true);
            v.setSku(("GLM-" + productId + "-" + size.getLabel() + "-" + color.getName()).toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "-"));
            try {
                variants.insert(c, v);
            } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                throw new BusinessException("That size and colour already exists for this product.");
            }
            return null;
        });
    }

    public void setStock(long productId, long variantId, int stock) {
        if (stock < 0 || stock > 100000) throw new BusinessException("Stock must be between 0 and 100000.");
        Tx.write(c -> {
            ProductVariant v = variants.findById(c, variantId);
            if (v == null || v.getProductId() != productId) throw new NotFoundException("Variant not found");
            variants.updateStock(c, variantId, stock);
            return null;
        });
    }

    public void setVariantActive(long productId, long variantId, boolean active) {
        Tx.write(c -> {
            ProductVariant v = variants.findById(c, variantId);
            if (v == null || v.getProductId() != productId) throw new NotFoundException("Variant not found");
            variants.setActive(c, variantId, active);
            return null;
        });
    }

    public void addSize(String label, int sortOrder) {
        label = Validator.clean(label);
        if (label.isEmpty() || label.length() > 20) throw new BusinessException("Enter a size label of up to 20 characters.");
        final String l = label;
        Tx.write(c -> {
            try { variants.insertSize(c, l, sortOrder); }
            catch (java.sql.SQLIntegrityConstraintViolationException e) { throw new BusinessException("That size already exists."); }
            return null;
        });
    }

    public void addColor(String name, String hex) {
        name = Validator.clean(name); hex = Validator.clean(hex);
        if (name.isEmpty() || name.length() > 40) throw new BusinessException("Enter a colour name of up to 40 characters.");
        if (!hex.matches("^#[0-9A-Fa-f]{6}$")) throw new BusinessException("Colour code must look like #A1B2C3.");
        final String n = name, h = hex;
        Tx.write(c -> {
            try { variants.insertColor(c, n, h); }
            catch (java.sql.SQLIntegrityConstraintViolationException e) { throw new BusinessException("That colour already exists."); }
            return null;
        });
    }

    // ----------------------------------------------------------- customers ---
    public Page<User> customers(String q, int page) {
        int size = 20;
        long total = Tx.read(c -> users.countCustomers(c, q));
        int pages = (int) Math.max(1, (total + size - 1) / size);
        int p = Math.max(1, Math.min(page, pages));
        return new Page<>(Tx.read(c -> users.customers(c, q, size, (p - 1) * size)), p, size, total);
    }

    public void setCustomerActive(long id, boolean active) { Tx.write(c -> { users.setActive(c, id, active); return null; }); }
}
