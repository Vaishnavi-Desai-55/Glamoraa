package com.glamoraa.util;

import java.math.BigDecimal;

/** Minimal JSON object builder for the few AJAX endpoints (cart, wishlist). */
public final class Json {
    private final StringBuilder sb = new StringBuilder("{");
    private boolean first = true;

    public static Json obj() { return new Json(); }

    private Json key(String k) {
        if (!first) sb.append(',');
        first = false;
        sb.append(quote(k)).append(':');
        return this;
    }

    public Json put(String k, String v) { key(k).sb.append(v == null ? "null" : quote(v)); return this; }
    public Json put(String k, long v) { key(k).sb.append(v); return this; }
    public Json put(String k, boolean v) { key(k).sb.append(v); return this; }
    public Json put(String k, BigDecimal v) { key(k).sb.append(v == null ? "null" : v.toPlainString()); return this; }
    public Json raw(String k, String json) { key(k).sb.append(json); return this; }

    public String end() { return sb.toString() + "}"; }
    @Override public String toString() { return end(); }

    public static String quote(String s) {
        StringBuilder o = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': o.append("\\\""); break;
                case '\\': o.append("\\\\"); break;
                case '\n': o.append("\\n"); break;
                case '\r': o.append("\\r"); break;
                case '\t': o.append("\\t"); break;
                case '<': o.append("\\u003c"); break;
                default:
                    if (c < 0x20) o.append(String.format("\\u%04x", (int) c)); else o.append(c);
            }
        }
        return o.append('"').toString();
    }
}
