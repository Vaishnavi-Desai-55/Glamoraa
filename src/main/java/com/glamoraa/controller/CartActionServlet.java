package com.glamoraa.controller;

import com.glamoraa.model.Cart;
import com.glamoraa.service.CartService;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GET  /cart/summary          cart as JSON for the drawer
 * POST /cart/add | update | remove | clear
 * Only ids and quantities are read from the browser. Prices, stock and totals are always computed on the server.
 */
@WebServlet(name = "CartActionServlet", urlPatterns = "/cart/*")
public class CartActionServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final CartService carts = new CartService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!"/summary".equals(req.getPathInfo())) throw new NotFoundException("Not found");
        Web.json(resp, 200, CartJson.of(carts.view(uid(req)), req.getContextPath(), null));
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long uid = uid(req);
        String action = req.getPathInfo() == null ? "" : req.getPathInfo();
        Cart cart;
        String message;
        switch (action) {
            case "/add":
                cart = carts.add(uid, Web.longParam(req, "productId", 0), Web.longParam(req, "variantId", 0), Web.intParam(req, "quantity", 1));
                message = "Added to your bag.";
                break;
            case "/update":
                cart = carts.updateQuantity(uid, Web.longParam(req, "itemId", 0), Web.intParam(req, "quantity", -1));
                message = "Bag updated.";
                break;
            case "/remove":
                cart = carts.remove(uid, Web.longParam(req, "itemId", 0));
                message = "Item removed.";
                break;
            case "/clear":
                carts.clear(uid);
                cart = carts.view(uid);
                message = "Your bag is empty.";
                break;
            default:
                throw new NotFoundException("Not found");
        }
        if (Web.wantsJson(req)) Web.json(resp, 200, CartJson.of(cart, req.getContextPath(), message));
        else flashAndRedirect(req, resp, message, "/add".equals(action) ? Web.backPath(req, "/cart") : "/cart");
    }
}
