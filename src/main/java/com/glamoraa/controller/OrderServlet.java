package com.glamoraa.controller;

import com.glamoraa.service.OrderService;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** /orders (history), /order-details, /order-confirmation and POST /order-cancel. Orders are always looked up by (id, session user). */
@WebServlet(name = "OrderServlet", urlPatterns = {"/orders", "/order-details", "/order-confirmation", "/order-cancel"})
public class OrderServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final OrderService orders = new OrderService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/orders":
                req.setAttribute("page", orders.history(uid(req), Web.intParam(req, "page", 1)));
                Web.view(req, resp, "orders/list");
                break;
            case "/order-details":
                req.setAttribute("order", orders.details(uid(req), Web.longParam(req, "id", 0)));
                Web.view(req, resp, "orders/details");
                break;
            case "/order-confirmation":
                req.setAttribute("order", orders.details(uid(req), Web.longParam(req, "id", 0)));
                Web.view(req, resp, "orders/confirmation");
                break;
            default:
                throw new NotFoundException("Not found");
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!"/order-cancel".equals(req.getServletPath())) { resp.sendError(405); return; }
        long id = Web.longParam(req, "id", 0);
        orders.cancel(uid(req), id);
        flashAndRedirect(req, resp, "Your order has been cancelled.", "/order-details?id=" + id);
    }
}
