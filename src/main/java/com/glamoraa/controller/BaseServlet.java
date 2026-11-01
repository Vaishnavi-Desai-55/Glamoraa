package com.glamoraa.controller;

import com.glamoraa.model.User;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.DataAccessException;
import com.glamoraa.util.Json;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Common behaviour: unexpected business errors become a friendly flash message (or JSON for fetch()),
 * missing records become 404, database failures become a generic 500 page. Technical detail is only logged.
 */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (NotFoundException e) {
            if (!resp.isCommitted()) resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (BusinessException e) {
            fail(req, resp, e.getMessage());
        } catch (DataAccessException e) {
            if (resp.isCommitted()) return;
            if (Web.wantsJson(req)) Web.json(resp, 500, Json.obj().put("ok", false).put("message", "Something went wrong on our side. Please try again.").end());
            else resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void fail(HttpServletRequest req, HttpServletResponse resp, String message) throws IOException {
        if (Web.wantsJson(req)) {
            Web.json(resp, 400, Json.obj().put("ok", false).put("message", message).end());
        } else {
            Web.flash(req, "error", message);
            Web.redirect(req, resp, Web.backPath(req, "/"));
        }
    }

    protected User user(HttpServletRequest req) { return Web.currentUser(req); }

    /** Authenticated user id, or 0 for visitors. */
    protected long uid(HttpServletRequest req) {
        User u = Web.currentUser(req);
        return u == null ? 0 : u.getId();
    }

    protected void flashAndRedirect(HttpServletRequest req, HttpServletResponse resp, String message, String path) throws IOException {
        Web.flash(req, "success", message);
        Web.redirect(req, resp, path);
    }
}
