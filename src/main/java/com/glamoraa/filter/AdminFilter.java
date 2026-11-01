package com.glamoraa.filter;

import com.glamoraa.model.User;
import com.glamoraa.service.AuthenticationService;
import com.glamoraa.util.Web;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Every /admin/* request is authorised on the server, against the database, not just the session copy. */
public class AdminFilter implements Filter {
    private final AuthenticationService auth = new AuthenticationService();

    @Override public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse out = (HttpServletResponse) resp;
        User sessionUser = Web.currentUser(r);
        if (sessionUser == null) {
            String target = r.getRequestURI().substring(r.getContextPath().length());
            Web.flash(r, "info", "Please log in to continue.");
            out.sendRedirect(r.getContextPath() + "/login?next=" + Web.enc("GET".equals(r.getMethod()) ? target : "/admin"));
            return;
        }
        User fresh = auth.findById(sessionUser.getId());
        if (fresh == null || !fresh.isActive() || !fresh.isAdmin()) {
            out.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        r.setAttribute("adminUser", fresh);
        chain.doFilter(req, resp);
    }
}
