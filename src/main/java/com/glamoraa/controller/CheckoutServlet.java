package com.glamoraa.controller;

import com.glamoraa.service.CheckoutService;
import com.glamoraa.service.CheckoutSummary;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/checkout")
public class CheckoutServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    public static final String TOKEN_KEY = "checkoutToken";
    private final CheckoutService checkout = new CheckoutService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CheckoutSummary s = checkout.prepare(uid(req));
        if (s.getCart().isEmpty()) {
            Web.flash(req, "info", "Your bag is empty. Add something you love first.");
            Web.redirect(req, resp, "/products");
            return;
        }
        if (!s.getCart().isCheckoutReady()) {
            Web.flash(req, "error", "Some items in your bag are unavailable or low on stock. Please review your bag.");
            Web.redirect(req, resp, "/cart");
            return;
        }
        // One-time token: submitting the form twice (double click, refresh, back button) can only ever create one order.
        String token = UUID.randomUUID().toString();
        req.getSession().setAttribute(TOKEN_KEY, token);
        req.setAttribute("summary", s);
        req.setAttribute("checkoutToken", token);
        Web.view(req, resp, "checkout/index");
    }
}
