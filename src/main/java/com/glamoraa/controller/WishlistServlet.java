package com.glamoraa.controller;

import com.glamoraa.service.WishlistService;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "WishlistServlet", urlPatterns = "/wishlist")
public class WishlistServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final WishlistService wishlist = new WishlistService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("wish", wishlist.view(uid(req)));
        Web.view(req, resp, "wishlist/index");
    }
}
