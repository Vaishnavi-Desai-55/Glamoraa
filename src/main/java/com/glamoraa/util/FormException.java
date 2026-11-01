package com.glamoraa.util;

import java.util.Map;

/** Validation failed: carries one message per field so the form can be shown again. */
public class FormException extends BusinessException {
    private static final long serialVersionUID = 1L;
    private final transient Map<String, String> errors;

    public FormException(Map<String, String> errors) {
        super("Please correct the highlighted fields.");
        this.errors = errors;
    }

    public Map<String, String> getErrors() { return errors; }
}
