package com.glamoraa.controller;

import com.glamoraa.service.AdminService;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Variant (size + colour) and stock management for one product, plus the shared size and colour lists. */
@WebServlet(name = "AdminVariantServlet", urlPatterns = "/admin/variants")
public class AdminVariantServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdminService admin = new AdminService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long productId = Web.longParam(req, "productId", 0);
        req.setAttribute("p", admin.product(productId));
        req.setAttribute("variants", admin.variants(productId));
        req.setAttribute("sizes", admin.sizes());
        req.setAttribute("colors", admin.colors());
        Web.view(req, resp, "admin/variants");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long productId = Web.longParam(req, "productId", 0);
        String message;
        switch (Web.param(req, "action")) {
            case "add":
                admin.addVariant(productId, Web.longParam(req, "sizeId", 0), Web.longParam(req, "colorId", 0), Web.intParam(req, "stock", -1));
                message = "Variant added.";
                break;
            case "stock":
                admin.setStock(productId, Web.longParam(req, "variantId", 0), Web.intParam(req, "stock", -1));
                message = "Stock updated.";
                break;
            case "toggle":
                admin.setVariantActive(productId, Web.longParam(req, "variantId", 0), "1".equals(req.getParameter("active")));
                message = "Variant updated.";
                break;
            case "addSize":
                admin.addSize(req.getParameter("label"), Web.intParam(req, "sortOrder", 100));
                message = "Size added.";
                break;
            case "addColor":
                admin.addColor(req.getParameter("name"), req.getParameter("hex"));
                message = "Colour added.";
                break;
            default:
                throw new BusinessException("Unknown action.");
        }
        flashAndRedirect(req, resp, message, "/admin/variants?productId=" + productId);
    }
}
