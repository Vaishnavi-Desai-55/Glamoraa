package com.glamoraa.util;

/** Requested record does not exist (or is not visible to this user). */
public class NotFoundException extends BusinessException {
    private static final long serialVersionUID = 1L;
    public NotFoundException(String message) { super(message); }
}
