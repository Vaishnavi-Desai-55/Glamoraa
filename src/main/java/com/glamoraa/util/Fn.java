package com.glamoraa.util;

import java.math.BigDecimal;

/** Static functions exposed to JSP as EL functions (see WEB-INF/tld/glamoraa.tld). */
public final class Fn {
    public static final String PLACEHOLDER = "/assets/images/placeholder.svg";

    private Fn() { }

    public static String money(BigDecimal v) { return Money.format(v); }

    /** Image path or the bundled placeholder when a product has no image. */
    public static String img(String path) { return path == null || path.isBlank() ? PLACEHOLDER : path; }

    public static String initial(String name) { return name == null || name.isBlank() ? "?" : name.substring(0, 1).toUpperCase(); }

    public static String encode(String s) { return s == null ? "" : Web.enc(s); }
}
