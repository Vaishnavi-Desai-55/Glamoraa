package com.glamoraa.controller;

import com.glamoraa.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Web.redirect(req, resp, "/");      // logging out is a state change, so it only happens on POST
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getSession(false) != null) req.getSession(false).invalidate();
        Web.flash(req, "success", "You have been logged out.");
        Web.redirect(req, resp, "/");
    }
}
