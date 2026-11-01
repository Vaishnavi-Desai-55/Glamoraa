package com.glamoraa.controller;

import com.glamoraa.service.ProductService;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final ProductService products = new ProductService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("home", products.home());
        Web.view(req, resp, "home/index");
    }
}
