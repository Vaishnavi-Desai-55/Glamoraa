package com.glamoraa.controller;

import com.glamoraa.service.ReviewService;
import com.glamoraa.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ReviewServlet", urlPatterns = "/product/review")
public class ReviewServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final ReviewService reviews = new ReviewService();

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long productId = Web.longParam(req, "productId", 0);
        reviews.submit(uid(req), productId, Web.intParam(req, "rating", 0), req.getParameter("title"), req.getParameter("body"));
        flashAndRedirect(req, resp, "Thank you - your review has been published.", "/product?id=" + productId + "#reviews");
    }
}
