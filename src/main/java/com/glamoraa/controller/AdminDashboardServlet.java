package com.glamoraa.controller;

import com.glamoraa.service.AdminService;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin", "/admin/dashboard"})
public class AdminDashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdminService admin = new AdminService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("dash", admin.dashboard());
        Web.view(req, resp, "admin/dashboard");
    }
}
