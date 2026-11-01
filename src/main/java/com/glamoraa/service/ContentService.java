package com.glamoraa.service;

import com.glamoraa.dao.EngagementDAO;
import com.glamoraa.dao.impl.EngagementDAOImpl;
import com.glamoraa.util.FormException;
import com.glamoraa.util.Tx;
import com.glamoraa.util.Validator;

/** Newsletter and contact form. Both are stored in MySQL (no email is sent - none is configured). */
public class ContentService {
    private final EngagementDAO engagement = new EngagementDAOImpl();

    public boolean subscribe(String email) {
        email = Validator.clean(email).toLowerCase();
        Validator v = new Validator().required("email", email, "Email", 190).email("email", email);
        if (!v.ok()) throw new FormException(v.errors());
        final String e = email;
        return Tx.write(c -> engagement.subscribe(c, e));
    }

    public void contact(String name, String email, String subject, String message) {
        name = Validator.clean(name); email = Validator.clean(email); subject = Validator.clean(subject); message = Validator.clean(message);
        Validator v = new Validator().required("name", name, "Name", 120).required("email", email, "Email", 190).email("email", email)
                .required("subject", subject, "Subject", 160).required("message", message, "Message", 3000);
        if (!v.ok()) throw new FormException(v.errors());
        final String n = name, e = email, s = subject, m = message;
        Tx.write(c -> { engagement.saveContactMessage(c, n, e, s, m); return null; });
    }
}
