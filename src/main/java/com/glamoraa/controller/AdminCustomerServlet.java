package com.glamoraa.controller;

import com.glamoraa.service.AdminService;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Customer list (never shows password hashes) with the ability to disable / re-enable an account. */
@WebServlet(name = "AdminCustomerServlet", urlPatterns = "/admin/customers")
public class AdminCustomerServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdminService admin = new AdminService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("page", admin.customers(Web.param(req, "q"), Web.intParam(req, "page", 1)));
        req.setAttribute("q", Web.param(req, "q"));
        Web.view(req, resp, "admin/customers");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        admin.setCustomerActive(Web.longParam(req, "id", 0), "1".equals(req.getParameter("active")));
        flashAndRedirect(req, resp, "Customer account updated.", Web.backPath(req, "/admin/customers"));
    }
}
