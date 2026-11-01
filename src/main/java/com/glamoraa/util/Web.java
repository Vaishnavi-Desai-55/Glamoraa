package com.glamoraa.util;

import com.glamoraa.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Small helpers shared by controllers and filters. */
public final class Web {
    public static final String USER_KEY = "authUser";
    public static final String FLASH_KEY = "flash";

    private Web() { }

    public static User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute(USER_KEY);
    }

    public static void flash(HttpServletRequest req, String type, String message) {
        req.getSession(true).setAttribute(FLASH_KEY, new String[]{type, message});
    }

    public static boolean wantsJson(HttpServletRequest req) {
        String a = req.getHeader("Accept");
        return "XMLHttpRequest".equals(req.getHeader("X-Requested-With")) || (a != null && a.contains("application/json"));
    }

    public static void json(HttpServletResponse resp, int status, String body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }

    public static void view(HttpServletRequest req, HttpServletResponse resp, String view) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    /** Only same-site relative paths are accepted (prevents open redirects). */
    public static String safeNext(HttpServletRequest req, String next, String fallback) {
        if (next == null || !next.startsWith("/") || next.startsWith("//") || next.contains("\\") || next.contains("\r") || next.contains("\n"))
            return fallback;
        String ctx = req.getContextPath();
        return (!ctx.isEmpty() && next.startsWith(ctx + "/")) ? next.substring(ctx.length()) : next;
    }

    public static long longParam(HttpServletRequest req, String name, long fallback) {
        try { return Long.parseLong(req.getParameter(name).trim()); } catch (RuntimeException e) { return fallback; }
    }

    public static int intParam(HttpServletRequest req, String name, int fallback) {
        try { return Integer.parseInt(req.getParameter(name).trim()); } catch (RuntimeException e) { return fallback; }
    }

    public static String param(HttpServletRequest req, String name) {
        return Validator.clean(req.getParameter(name));
    }

    /** URL-encodes a query value. */
    public static String enc(String s) {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /** Where to send the visitor after an action: the page they came from (same host only) or a fallback. */
    public static String backPath(HttpServletRequest req, String fallback) {
        String ref = req.getHeader("Referer");
        if (ref == null) return fallback;
        try {
            java.net.URI u = java.net.URI.create(ref);
            if (u.getHost() == null || !u.getHost().equalsIgnoreCase(req.getServerName())) return fallback;
            String path = u.getRawPath() + (u.getRawQuery() == null ? "" : "?" + u.getRawQuery());
            return safeNext(req, path, fallback);
        } catch (RuntimeException e) {
            return fallback;
        }
    }
}
