package com.glamoraa.util;

import java.text.Normalizer;
import java.util.Locale;

public final class Slugs {
    private Slugs() { }

    public static String of(String text) {
        String s = Normalizer.normalize(text == null ? "" : text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
        return s.isEmpty() ? "item" : (s.length() > 90 ? s.substring(0, 90) : s);
    }
}
