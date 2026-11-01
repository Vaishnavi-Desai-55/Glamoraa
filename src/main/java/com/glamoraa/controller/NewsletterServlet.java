package com.glamoraa.controller;

import com.glamoraa.service.ContentService;
import com.glamoraa.util.FormException;
import com.glamoraa.util.Json;
import com.glamoraa.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "NewsletterServlet", urlPatterns = "/newsletter")
public class NewsletterServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final ContentService content = new ContentService();

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String msg;
        boolean ok = true;
        try {
            msg = content.subscribe(req.getParameter("email")) ? "You are subscribed. Welcome to Glamoraa." : "You are already subscribed.";
        } catch (FormException e) {
            ok = false;
            msg = e.getErrors().values().iterator().next();
        }
        if (Web.wantsJson(req)) { Web.json(resp, ok ? 200 : 400, Json.obj().put("ok", ok).put("message", msg).end()); return; }
        Web.flash(req, ok ? "success" : "error", msg);
        Web.redirect(req, resp, Web.backPath(req, "/"));
    }
}
