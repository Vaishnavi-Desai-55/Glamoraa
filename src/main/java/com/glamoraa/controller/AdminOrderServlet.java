package com.glamoraa.controller;

import com.glamoraa.service.OrderService;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/** /admin/orders (list + filters), /admin/orders/view?id=, POST /admin/orders/status. */
@WebServlet(name = "AdminOrderServlet", urlPatterns = "/admin/orders/*")
public class AdminOrderServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final Set<String> STATUSES = Set.of("PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");
    private final OrderService orders = new OrderService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
        if ("/".equals(path)) {
            String status = Web.param(req, "status");
            if (!STATUSES.contains(status)) status = "";
            req.setAttribute("page", orders.adminList(status, Web.param(req, "q"), Web.intParam(req, "page", 1)));
            req.setAttribute("status", status);
            req.setAttribute("q", Web.param(req, "q"));
            Web.view(req, resp, "admin/orders");
        } else if ("/view".equals(path)) {
            req.setAttribute("order", orders.adminDetails(Web.longParam(req, "id", 0)));
            Web.view(req, resp, "admin/order-details");
        } else {
            throw new NotFoundException("Not found");
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!"/status".equals(req.getPathInfo())) throw new NotFoundException("Not found");
        long id = Web.longParam(req, "id", 0);
        String status = Web.param(req, "status");
        if (!STATUSES.contains(status)) throw new com.glamoraa.util.BusinessException("Choose a valid status.");
        orders.adminChangeStatus(id, status);
        flashAndRedirect(req, resp, "Order marked as " + status.toLowerCase() + ".", "/admin/orders/view?id=" + id);
    }
}
