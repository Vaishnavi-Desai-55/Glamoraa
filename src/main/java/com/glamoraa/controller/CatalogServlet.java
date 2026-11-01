package com.glamoraa.controller;

import com.glamoraa.dao.ProductFilter;
import com.glamoraa.model.Category;
import com.glamoraa.model.Product;
import com.glamoraa.service.ProductService;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Page;
import com.glamoraa.util.Validator;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

/** /products (shop + filters), /category/{slug} (category listing) and /search?q= share one catalog view. */
@WebServlet(name = "CatalogServlet", urlPatterns = {"/products", "/category/*", "/search"})
public class CatalogServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final Set<String> SORTS = Set.of("newest", "price_asc", "price_desc", "name_asc", "name_desc");
    private final ProductService products = new ProductService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        ProductFilter f = new ProductFilter();

        Category category = null;
        String slug = "/category".equals(servletPath) ? Validator.clean(req.getPathInfo()).replace("/", "") : Web.param(req, "category");
        if (!slug.isEmpty()) {
            category = products.categoryBySlug(slug);
            if (category == null) throw new NotFoundException("Category not found");
            f.categoryId = category.getId();
        }
        String q = Web.param(req, "q");
        if (q.length() > 100) q = q.substring(0, 100);
        f.query = q.isEmpty() ? null : q;
        f.minPrice = ProductService.parseMoneyOrNull(Web.param(req, "min"));
        f.maxPrice = ProductService.parseMoneyOrNull(Web.param(req, "max"));
        if (f.minPrice != null && f.maxPrice != null && f.minPrice.compareTo(f.maxPrice) > 0) {
            BigDecimal t = f.minPrice; f.minPrice = f.maxPrice; f.maxPrice = t;
        }
        String sort = Web.param(req, "sort");
        f.sort = SORTS.contains(sort) ? sort : "newest";
        f.newOnly = "1".equals(req.getParameter("new"));
        f.featuredOnly = "1".equals(req.getParameter("featured"));

        Page<Product> page = products.catalog(f, Web.intParam(req, "page", 1));

        // Base URL (without page=) so pagination links keep every filter and stay shareable.
        String base = "/search".equals(servletPath) ? "/search" : "/category".equals(servletPath) ? "/category/" + category.getSlug() : "/products";
        StringBuilder qs = new StringBuilder();
        if (category != null && !"/category".equals(servletPath)) qs.append("category=").append(Web.enc(category.getSlug())).append('&');
        if (f.query != null) qs.append("q=").append(Web.enc(f.query)).append('&');
        if (f.minPrice != null) qs.append("min=").append(f.minPrice.toPlainString()).append('&');
        if (f.maxPrice != null) qs.append("max=").append(f.maxPrice.toPlainString()).append('&');
        if (!"newest".equals(f.sort)) qs.append("sort=").append(f.sort).append('&');
        if (f.newOnly) qs.append("new=1&");
        if (f.featuredOnly) qs.append("featured=1&");

        String heading = "/search".equals(servletPath) ? "Search" : f.newOnly ? "New arrivals" : f.featuredOnly ? "The featured collection" : category != null ? category.getName() : "Shop all";
        req.setAttribute("page", page);
        req.setAttribute("category", category);
        req.setAttribute("heading", heading);
        req.setAttribute("isSearch", "/search".equals(servletPath));
        req.setAttribute("q", f.query == null ? "" : f.query);
        req.setAttribute("min", f.minPrice == null ? "" : f.minPrice.toPlainString());
        req.setAttribute("max", f.maxPrice == null ? "" : f.maxPrice.toPlainString());
        req.setAttribute("sort", f.sort);
        req.setAttribute("newOnly", f.newOnly);
        req.setAttribute("featuredOnly", f.featuredOnly);
        req.setAttribute("formAction", req.getContextPath() + base);
        req.setAttribute("pageBase", req.getContextPath() + base + "?" + qs);
        req.setAttribute("filtered", f.query != null || f.minPrice != null || f.maxPrice != null);
        Web.view(req, resp, "product/list");
    }
}
