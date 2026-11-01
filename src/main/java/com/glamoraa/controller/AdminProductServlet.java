package com.glamoraa.controller;

import com.glamoraa.model.Product;
import com.glamoraa.service.AdminService;
import com.glamoraa.util.FormException;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/** /admin/products (list), /new, /edit?id=, POST /save and /toggle. */
@WebServlet(name = "AdminProductServlet", urlPatterns = "/admin/products/*")
public class AdminProductServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdminService admin = new AdminService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (path) {
            case "/":
                req.setAttribute("page", admin.products(Web.param(req, "q"), Web.longParam(req, "category", 0), Web.intParam(req, "page", 1)));
                req.setAttribute("categories", admin.allCategories());
                req.setAttribute("q", Web.param(req, "q"));
                req.setAttribute("categoryFilter", Web.longParam(req, "category", 0));
                Web.view(req, resp, "admin/products");
                break;
            case "/new": {
                Product p = new Product();
                p.setActive(true); p.setPrice(BigDecimal.ZERO);
                showForm(req, resp, p, "");
                break;
            }
            case "/edit": {
                Product p = admin.product(Web.longParam(req, "id", 0));
                showForm(req, resp, p, p.getPrice().toPlainString());
                break;
            }
            default:
                throw new NotFoundException("Not found");
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        if ("/toggle".equals(path)) {
            admin.setProductActive(Web.longParam(req, "id", 0), "1".equals(req.getParameter("active")));
            flashAndRedirect(req, resp, "Product visibility updated.", Web.backPath(req, "/admin/products"));
            return;
        }
        if (!"/save".equals(path)) throw new NotFoundException("Not found");

        Product p = new Product();
        p.setId(Web.longParam(req, "id", 0));
        p.setName(Web.param(req, "name"));
        p.setCategoryId(Web.longParam(req, "categoryId", 0));
        p.setDescription(Web.param(req, "description"));
        p.setSpecifications(Web.param(req, "specifications"));
        p.setDiscountPercent(Web.intParam(req, "discountPercent", 0));
        p.setFeatured(req.getParameter("featured") != null);
        p.setNewArrival(req.getParameter("newArrival") != null);
        p.setActive(req.getParameter("active") != null);
        String priceText = Web.param(req, "price");
        try {
            boolean isNew = p.getId() == 0;
            long id = admin.saveProduct(p, priceText);
            flashAndRedirect(req, resp, isNew ? "Product created. Now add its images and size/colour variants." : "Product saved.",
                    isNew ? "/admin/images?productId=" + id : "/admin/products");
        } catch (FormException e) {
            req.setAttribute("errors", e.getErrors());
            showForm(req, resp, p, priceText);
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Product p, String priceText) throws ServletException, IOException {
        req.setAttribute("p", p);
        req.setAttribute("priceText", priceText);
        req.setAttribute("categories", admin.allCategories());
        Web.view(req, resp, "admin/product-form");
    }
}
