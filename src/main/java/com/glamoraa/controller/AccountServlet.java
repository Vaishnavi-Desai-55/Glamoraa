package com.glamoraa.controller;

import com.glamoraa.model.Address;
import com.glamoraa.service.AddressService;
import com.glamoraa.service.OrderService;
import com.glamoraa.service.WishlistService;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AccountServlet", urlPatterns = "/account")
public class AccountServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final OrderService orders = new OrderService();
    private final AddressService addresses = new AddressService();
    private final WishlistService wishlist = new WishlistService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long uid = uid(req);
        req.setAttribute("recentOrders", orders.history(uid, 1).getItems().stream().limit(3).toList());
        Address def = addresses.list(uid).stream().filter(Address::isDefaultAddress).findFirst().orElse(null);
        req.setAttribute("defaultAddress", def);
        req.setAttribute("wishlistCount", wishlist.ids(uid).size());
        Web.view(req, resp, "account/dashboard");
    }
}
