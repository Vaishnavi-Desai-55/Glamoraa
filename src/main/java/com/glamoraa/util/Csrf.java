package com.glamoraa.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Per-session CSRF token. Forms send it as the hidden field "_csrf"; fetch() sends the X-CSRF-Token header. */
public final class Csrf {
    public static final String SESSION_KEY = "csrfToken";
    public static final String FIELD = "_csrf";
    public static final String HEADER = "X-CSRF-Token";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Csrf() { }

    public static String token(HttpSession session) {
        String t = (String) session.getAttribute(SESSION_KEY);
        if (t == null) {
            byte[] b = new byte[32];
            RANDOM.nextBytes(b);
            t = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
            session.setAttribute(SESSION_KEY, t);
        }
        return t;
    }

    public static boolean isValid(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return false;
        String expected = (String) s.getAttribute(SESSION_KEY);
        String given = req.getHeader(HEADER);
        if (given == null || given.isEmpty()) given = req.getParameter(FIELD);
        if (expected == null || given == null) return false;
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), given.getBytes(StandardCharsets.UTF_8));
    }
}
