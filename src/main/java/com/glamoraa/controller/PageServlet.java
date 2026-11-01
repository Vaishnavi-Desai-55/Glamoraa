package com.glamoraa.controller;

import com.glamoraa.service.ContentService;
import com.glamoraa.util.FormException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Static content pages, plus the contact form (stored in the database). */
@WebServlet(name = "PageServlet", urlPatterns = {"/about", "/contact", "/shipping-returns", "/privacy", "/terms"})
public class PageServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final ContentService content = new ContentService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Web.view(req, resp, "pages/" + req.getServletPath().substring(1));
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/contact".equals(req.getServletPath())) { resp.sendError(405); return; }
        try {
            content.contact(req.getParameter("name"), req.getParameter("email"), req.getParameter("subject"), req.getParameter("message"));
            flashAndRedirect(req, resp, "Thanks for reaching out. We have received your message.", "/contact");
        } catch (FormException e) {
            req.setAttribute("errors", e.getErrors());
            req.setAttribute("form", req.getParameterMap());
            Web.view(req, resp, "pages/contact");
        }
    }
}
