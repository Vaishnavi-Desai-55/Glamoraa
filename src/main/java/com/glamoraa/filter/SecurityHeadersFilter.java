package com.glamoraa.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Defence-in-depth headers. The CSP allows only our own scripts (no inline JS) plus Google Fonts. */
public class SecurityHeadersFilter implements Filter {
    private static final String CSP = "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; "
            + "font-src 'self' https://fonts.gstatic.com; img-src 'self' data:; connect-src 'self'; frame-ancestors 'self'; base-uri 'self'; form-action 'self'";

    @Override public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse r = (HttpServletResponse) resp;
        r.setHeader("X-Content-Type-Options", "nosniff");
        r.setHeader("X-Frame-Options", "SAMEORIGIN");
        r.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        r.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
        r.setHeader("Content-Security-Policy", CSP);
        chain.doFilter(req, resp);
    }
}
