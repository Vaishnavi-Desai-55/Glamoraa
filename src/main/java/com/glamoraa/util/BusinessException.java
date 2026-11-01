package com.glamoraa.util;

/** A rule violation whose message is safe to show to the customer. */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public BusinessException(String message) { super(message); }
}
