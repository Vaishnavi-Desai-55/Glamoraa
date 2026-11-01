package com.glamoraa.util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** Collects field errors so a form can be re-displayed with every problem at once. */
public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9 \\-]{7,18}$");
    private static final Pattern POSTAL = Pattern.compile("^[A-Za-z0-9 \\-]{4,10}$");

    private final Map<String, String> errors = new LinkedHashMap<>();

    public static String clean(String s) { return s == null ? "" : s.trim(); }

    public Validator required(String field, String value, String label, int max) {
        if (value == null || value.isBlank()) errors.put(field, label + " is required.");
        else if (value.length() > max) errors.put(field, label + " must be " + max + " characters or fewer.");
        return this;
    }

    public Validator optional(String field, String value, String label, int max) {
        if (value != null && value.length() > max) errors.put(field, label + " must be " + max + " characters or fewer.");
        return this;
    }

    public Validator email(String field, String value) {
        if (!errors.containsKey(field) && (value == null || !EMAIL.matcher(value).matches() || value.length() > 190))
            errors.put(field, "Enter a valid email address.");
        return this;
    }

    public Validator phone(String field, String value, boolean mandatory) {
        if (errors.containsKey(field)) return this;
        if (value == null || value.isBlank()) { if (mandatory) errors.put(field, "Phone number is required."); }
        else if (!PHONE.matcher(value).matches()) errors.put(field, "Enter a valid phone number (7-15 digits).");
        return this;
    }

    public Validator postal(String field, String value) {
        if (!errors.containsKey(field) && !POSTAL.matcher(value == null ? "" : value).matches())
            errors.put(field, "Enter a valid postal code.");
        return this;
    }

    public Validator password(String field, String value) {
        if (value == null || value.length() < 8 || value.length() > 100
                || !value.matches(".*[A-Za-z].*") || !value.matches(".*[0-9].*"))
            errors.put(field, "Use 8-100 characters with at least one letter and one number.");
        return this;
    }

    public Validator money(String field, String value, String label) {
        try {
            BigDecimal d = new BigDecimal(value);
            if (d.signum() < 0 || d.compareTo(new BigDecimal("9999999")) > 0 || d.scale() > 2) throw new NumberFormatException();
        } catch (RuntimeException e) {
            errors.put(field, label + " must be a positive amount with at most 2 decimals.");
        }
        return this;
    }

    public Validator range(String field, int value, int min, int max, String label) {
        if (value < min || value > max) errors.put(field, label + " must be between " + min + " and " + max + ".");
        return this;
    }

    public Validator error(String field, String message) { errors.put(field, message); return this; }
    public boolean ok() { return errors.isEmpty(); }
    public Map<String, String> errors() { return errors; }
}
