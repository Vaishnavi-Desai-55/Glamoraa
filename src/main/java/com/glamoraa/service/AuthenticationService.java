package com.glamoraa.service;

import com.glamoraa.dao.UserDAO;
import com.glamoraa.dao.impl.UserDAOImpl;
import com.glamoraa.model.User;
import com.glamoraa.util.AppConfig;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.FormException;
import com.glamoraa.util.PasswordUtil;
import com.glamoraa.util.Tx;
import com.glamoraa.util.Validator;
import java.util.logging.Logger;

public class AuthenticationService {
    private static final Logger LOG = Logger.getLogger(AuthenticationService.class.getName());
    private final UserDAO users = new UserDAOImpl();

    private static User safeCopy(User u) {
        u.setPasswordHash(null);   // the hash never lives in the session or in JSPs
        return u;
    }

    /** Role is ALWAYS CUSTOMER here - there is no way to ask for ADMIN through the public form. */
    public User register(String name, String email, String phone, String password, String confirm) {
        name = Validator.clean(name);
        email = Validator.clean(email).toLowerCase();
        phone = Validator.clean(phone);
        Validator v = new Validator().required("fullName", name, "Full name", 120).required("email", email, "Email", 190).email("email", email)
                .phone("phone", phone, false).password("password", password);
        if (v.ok() && !password.equals(confirm)) v.error("confirmPassword", "Passwords do not match.");
        if (!v.ok()) throw new FormException(v.errors());

        final String n = name, e = email, p = phone.isEmpty() ? null : phone;
        return Tx.write(c -> {
            if (users.emailExists(c, e)) throw new FormException(new Validator().error("email", "An account with this email already exists.").errors());
            User u = new User();
            u.setFullName(n); u.setEmail(e); u.setPhone(p); u.setRole("CUSTOMER");
            u.setPasswordHash(PasswordUtil.hash(password));
            try {
                u.setId(users.insert(c, u));
            } catch (java.sql.SQLIntegrityConstraintViolationException dup) {
                throw new FormException(new Validator().error("email", "An account with this email already exists.").errors());
            }
            return safeCopy(u);
        });
    }

    /** Same generic message for unknown email, wrong password and disabled account. */
    public User login(String email, String password) {
        final String e = Validator.clean(email).toLowerCase();
        User u = Tx.read(c -> users.findByEmail(c, e));
        if (u == null || password == null) {
            PasswordUtil.verifyDummy(password == null ? "" : password);
            throw new BusinessException("Invalid email or password.");
        }
        if (!PasswordUtil.verify(password, u.getPasswordHash()) || !u.isActive()) throw new BusinessException("Invalid email or password.");
        return safeCopy(u);
    }

    public User findById(long id) {
        User u = Tx.read(c -> users.findById(c, id));
        return u == null ? null : safeCopy(u);
    }

    public User updateProfile(long userId, String name, String phone) {
        name = Validator.clean(name);
        phone = Validator.clean(phone);
        Validator v = new Validator().required("fullName", name, "Full name", 120).phone("phone", phone, false);
        if (!v.ok()) throw new FormException(v.errors());
        final String n = name, p = phone.isEmpty() ? null : phone;
        Tx.write(c -> { users.updateProfile(c, userId, n, p); return null; });
        return findById(userId);
    }

    public void changePassword(long userId, String current, String next, String confirm) {
        Validator v = new Validator().password("newPassword", next);
        if (v.ok() && !next.equals(confirm)) v.error("confirmPassword", "Passwords do not match.");
        if (!v.ok()) throw new FormException(v.errors());
        Tx.write(c -> {
            User u = users.findById(c, userId);
            if (u == null || current == null || !PasswordUtil.verify(current, u.getPasswordHash()))
                throw new FormException(new Validator().error("currentPassword", "Current password is incorrect.").errors());
            users.updatePassword(c, userId, PasswordUtil.hash(next));
            return null;
        });
    }

    /** Creates the first admin from configuration (env var or local properties) - no default password in code. */
    public void bootstrapAdmin() {
        String email = AppConfig.get("admin.bootstrap.email").toLowerCase();
        String password = AppConfig.get("admin.bootstrap.password");
        if (email.isEmpty() || password.isEmpty()) return;
        Tx.write(c -> {
            if (users.countAdmins(c) > 0) return null;
            if (users.emailExists(c, email)) { LOG.warning("Admin bootstrap skipped: that email already belongs to a customer."); return null; }
            User u = new User();
            u.setFullName(AppConfig.get("admin.bootstrap.name").isEmpty() ? "Administrator" : AppConfig.get("admin.bootstrap.name"));
            u.setEmail(email); u.setRole("ADMIN"); u.setPasswordHash(PasswordUtil.hash(password));
            users.insert(c, u);
            LOG.info("Bootstrap admin account created for " + email);
            return null;
        });
    }
}
