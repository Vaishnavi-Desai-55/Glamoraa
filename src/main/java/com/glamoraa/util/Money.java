package com.glamoraa.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/** Rupee formatting with Indian digit grouping: whole amounts show no decimals, others show two. */
public final class Money {
    private Money() { }

    public static String format(BigDecimal v) {
        if (v == null) return "";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"));
        boolean whole = v.stripTrailingZeros().scale() <= 0;
        nf.setMinimumFractionDigits(whole ? 0 : 2);
        nf.setMaximumFractionDigits(whole ? 0 : 2);
        return "\u20B9" + nf.format(v);
    }
}
