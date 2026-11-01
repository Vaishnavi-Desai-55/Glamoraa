package com.glamoraa.util;

/** Wraps SQLException. Technical details are logged server-side; customers only see a generic message. */
public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
