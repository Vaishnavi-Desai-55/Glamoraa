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

@WebServlet(name = "ProfileServlet", urlPatterns = "/account/profile")
public class ProfileServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AuthenticationService auth = new AuthenticationService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Web.view(req, resp, "account/profile");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = Web.param(req, "action");
        try {
            if ("password".equals(action)) {
                auth.changePassword(uid(req), req.getParameter("currentPassword"), req.getParameter("newPassword"), req.getParameter("confirmPassword"));
                flashAndRedirect(req, resp, "Your password has been changed.", "/account/profile");
            } else {
                User updated = auth.updateProfile(uid(req), req.getParameter("fullName"), req.getParameter("phone"));
                req.getSession().setAttribute(Web.USER_KEY, updated);      // identity still comes from the session, refreshed here
                flashAndRedirect(req, resp, "Your profile has been updated.", "/account/profile");
            }
        } catch (FormException e) {
            req.setAttribute("errors", e.getErrors());
            req.setAttribute("formAction", action);
            req.setAttribute("fullNameInput", Web.param(req, "fullName"));
            req.setAttribute("phoneInput", Web.param(req, "phone"));
            Web.view(req, resp, "account/profile");
        }
    }
}
