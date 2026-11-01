package com.glamoraa.filter;

import com.glamoraa.util.Json;
import com.glamoraa.util.Web;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Customer area guard. The identity comes from the server-side session only. */
public class AuthFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse out = (HttpServletResponse) resp;
        if (Web.currentUser(r) != null) { chain.doFilter(req, resp); return; }

        String target = r.getRequestURI().substring(r.getContextPath().length());
        String next = "GET".equals(r.getMethod()) ? target + (r.getQueryString() == null ? "" : "?" + r.getQueryString())
                : Web.safeNext(r, Web.backPath(r, "/"), "/");
        String login = r.getContextPath() + "/login?next=" + Web.enc(next);
        if (Web.wantsJson(r)) {
            Web.json(out, 401, Json.obj().put("ok", false).put("login", login).put("message", "Please log in to continue.").end());
        } else {
            Web.flash(r, "info", "Please log in to continue.");
            out.sendRedirect(login);
        }
    }
}
