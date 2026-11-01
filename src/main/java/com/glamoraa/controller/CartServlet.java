package com.glamoraa.controller;

import com.glamoraa.model.Cart;
import com.glamoraa.service.CartService;
import com.glamoraa.service.PricingService;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "CartServlet", urlPatterns = "/cart")
public class CartServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final CartService carts = new CartService();
    private final PricingService pricing = new PricingService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cart cart = carts.view(uid(req));
        BigDecimal shipping = pricing.shippingFor(cart.getSubtotal());
        req.setAttribute("cart", cart);
        req.setAttribute("shipping", shipping);
        req.setAttribute("total", cart.getSubtotal().add(shipping));
        BigDecimal toFree = pricing.freeShippingThreshold().subtract(cart.getSubtotal());
        req.setAttribute("toFreeShipping", toFree.signum() > 0 && !cart.isEmpty() ? toFree : null);
        Web.view(req, resp, "cart/index");
    }
}
