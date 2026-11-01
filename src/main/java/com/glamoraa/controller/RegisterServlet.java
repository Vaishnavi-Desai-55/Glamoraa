package com.glamoraa.controller;

import com.glamoraa.model.User;
import com.glamoraa.service.AuthenticationService;
import com.glamoraa.util.FormException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AuthenticationService auth = new AuthenticationService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (user(req) != null) { Web.redirect(req, resp, "/account"); return; }
        Web.view(req, resp, "auth/register");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Only name, email, phone and passwords are read. A role can never be supplied by the browser.
            User u = auth.register(req.getParameter("fullName"), req.getParameter("email"), req.getParameter("phone"),
                    req.getParameter("password"), req.getParameter("confirmPassword"));
            req.getSession(true);
            req.changeSessionId();
            req.getSession().setAttribute(Web.USER_KEY, u);
            flashAndRedirect(req, resp, "Welcome to Glamoraa, " + u.getFullName().split(" ")[0] + ". Your account is ready.", "/account");
        } catch (FormException e) {
            req.setAttribute("errors", e.getErrors());
            req.setAttribute("fullName", Web.param(req, "fullName"));
            req.setAttribute("email", Web.param(req, "email"));
            req.setAttribute("phone", Web.param(req, "phone"));
            Web.view(req, resp, "auth/register");
        }
    }
}
