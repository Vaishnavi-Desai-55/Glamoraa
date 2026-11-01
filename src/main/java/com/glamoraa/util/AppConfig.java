package com.glamoraa.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central configuration. Priority: environment variable (GLAMORAA_*) &gt;
 * glamoraa-local.properties &gt; glamoraa.properties. Secrets never live in source code.
 */
public final class AppConfig {
    private static final Logger LOG = Logger.getLogger(AppConfig.class.getName());
    private static final Properties PROPS = new Properties();

    static {
        load("glamoraa.properties");
        load("glamoraa-local.properties");
    }

    private AppConfig() { }

    private static void load(String name) {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(name)) {
            if (in != null) PROPS.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Could not read " + name, e);
        }
    }

    public static String get(String key) {
        String env = System.getenv("GLAMORAA_" + key.toUpperCase().replace('.', '_'));
        if (env != null && !env.isBlank()) return env.trim();
        String v = PROPS.getProperty(key);
        return v == null ? "" : v.trim();
    }

    public static int getInt(String key, int fallback) {
        try { return Integer.parseInt(get(key)); } catch (NumberFormatException e) { return fallback; }
    }

    public static BigDecimal getMoney(String key, BigDecimal fallback) {
        try { return new BigDecimal(get(key)); } catch (NumberFormatException e) { return fallback; }
    }
}
