package com.glamoraa.util;

import java.util.concurrent.ConcurrentHashMap;

/** Slows down password guessing: 5 failures for the same IP + email lock further attempts for 5 minutes. */
public final class LoginThrottle {
    private static final int MAX_FAILURES = 5;
    private static final long LOCK_MS = 5 * 60_000L;
    private static final ConcurrentHashMap<String, long[]> STATE = new ConcurrentHashMap<>();   // {failures, firstFailureMillis}

    private LoginThrottle() { }

    private static String key(String ip, String email) { return ip + "|" + email.toLowerCase(); }

    public static boolean isLocked(String ip, String email) {
        long[] s = STATE.get(key(ip, email));
        if (s == null) return false;
        if (System.currentTimeMillis() - s[1] > LOCK_MS) { STATE.remove(key(ip, email)); return false; }
        return s[0] >= MAX_FAILURES;
    }

    public static void failed(String ip, String email) {
        if (STATE.size() > 10_000) STATE.clear();
        STATE.merge(key(ip, email), new long[]{1, System.currentTimeMillis()}, (o, n) -> {
            if (System.currentTimeMillis() - o[1] > LOCK_MS) return n;
            o[0]++;
            return o;
        });
    }

    public static void succeeded(String ip, String email) { STATE.remove(key(ip, email)); }
}
