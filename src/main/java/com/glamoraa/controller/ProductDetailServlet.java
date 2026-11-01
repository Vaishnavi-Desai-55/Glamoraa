package com.glamoraa.controller;

import com.glamoraa.service.ProductDetail;
import com.glamoraa.service.ProductService;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProductDetailServlet", urlPatterns = "/product")
public class ProductDetailServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final ProductService products = new ProductService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Web.longParam(req, "id", 0);
        if (id <= 0) throw new NotFoundException("Product not found");
        ProductDetail d = products.detail(id, uid(req));
        req.setAttribute("detail", d);
        req.setAttribute("p", d.getProduct());
        Web.view(req, resp, "product/detail");
    }
}
