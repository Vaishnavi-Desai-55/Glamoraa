package com.glamoraa.controller;

import com.glamoraa.model.User;
import com.glamoraa.service.AuthenticationService;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.LoginThrottle;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AuthenticationService auth = new AuthenticationService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (user(req) != null) { Web.redirect(req, resp, "/account"); return; }
        req.setAttribute("next", Web.safeNext(req, req.getParameter("next"), ""));
        Web.view(req, resp, "auth/login");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = Web.param(req, "email");
        String next = Web.safeNext(req, req.getParameter("next"), "");
        String ip = req.getRemoteAddr();
        try {
            if (LoginThrottle.isLocked(ip, email)) throw new BusinessException("Too many attempts. Please wait a few minutes and try again.");
            User u;
            try {
                u = auth.login(email, req.getParameter("password"));
            } catch (BusinessException e) {
                LoginThrottle.failed(ip, email);
                throw e;
            }
            LoginThrottle.succeeded(ip, email);
            req.getSession(true);
            req.changeSessionId();                         // new session id after authentication (prevents session fixation)
            req.getSession().setAttribute(Web.USER_KEY, u);
            Web.flash(req, "success", "Welcome back, " + u.getFullName().split(" ")[0] + ".");
            Web.redirect(req, resp, !next.isEmpty() ? next : u.isAdmin() ? "/admin" : "/account");
        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("next", next);
            Web.view(req, resp, "auth/login");
        }
    }
}
