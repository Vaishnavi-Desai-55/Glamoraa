package com.glamoraa.controller;

import com.glamoraa.model.Order;
import com.glamoraa.service.CheckoutService;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "PlaceOrderServlet", urlPatterns = "/place-order")
public class PlaceOrderServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final CheckoutService checkout = new CheckoutService();

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String expected = (String) session.getAttribute(CheckoutServlet.TOKEN_KEY);
        String given = Web.param(req, "checkoutToken");
        session.removeAttribute(CheckoutServlet.TOKEN_KEY);          // consumed: a replay finds nothing to match
        if (expected == null || !expected.equals(given)) {
            Web.flash(req, "info", "That checkout was already submitted or has expired. Please check your orders.");
            Web.redirect(req, resp, "/orders");
            return;
        }
        try {
            Order o = checkout.placeOrder(uid(req), Web.longParam(req, "addressId", 0), Web.param(req, "paymentMethod"), given);
            Web.redirect(req, resp, "/order-confirmation?id=" + o.getId());
        } catch (BusinessException e) {
            Web.flash(req, "error", e.getMessage());
            Web.redirect(req, resp, "/checkout");
        }
    }
}
