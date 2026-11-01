package com.glamoraa.filter;

import com.glamoraa.util.Csrf;
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
import java.util.Set;
import java.util.logging.Logger;

/** Rejects every state-changing request that does not carry the session's CSRF token. */
public class CsrfFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(CsrfFilter.class.getName());
    private static final Set<String> SAFE = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    @Override public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        if (SAFE.contains(r.getMethod()) || Csrf.isValid(r)) {
            chain.doFilter(req, resp);
            return;
        }
        LOG.warning("CSRF token rejected for " + r.getMethod() + " " + r.getRequestURI());
        HttpServletResponse out = (HttpServletResponse) resp;
        if (Web.wantsJson(r)) Web.json(out, 403, Json.obj().put("ok", false).put("message", "Your session expired. Refresh the page and try again.").end());
        else out.sendError(403);
    }
}
