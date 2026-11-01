package com.glamoraa.controller;

import com.glamoraa.model.Address;
import com.glamoraa.service.AddressService;
import com.glamoraa.util.FormException;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GET  /account/addresses            list
 * GET  /account/addresses/new        empty form
 * GET  /account/addresses/edit?id=   edit form (only the owner's address is ever loaded)
 * POST /account/addresses/save | delete | default
 */
@WebServlet(name = "AddressServlet", urlPatterns = "/account/addresses/*")
public class AddressServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AddressService addresses = new AddressService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
        long uid = uid(req);
        switch (path) {
            case "/":
                req.setAttribute("addresses", addresses.list(uid));
                Web.view(req, resp, "address/list");
                break;
            case "/new": {
                Address a = new Address();
                a.setCountry("India"); a.setLabel("Home");
                a.setRecipientName(user(req).getFullName()); a.setPhone(user(req).getPhone());
                req.setAttribute("address", a);
                req.setAttribute("next", Web.safeNext(req, req.getParameter("next"), ""));
                Web.view(req, resp, "address/form");
                break;
            }
            case "/edit":
                req.setAttribute("address", addresses.get(uid, Web.longParam(req, "id", 0)));
                Web.view(req, resp, "address/form");
                break;
            default:
                throw new NotFoundException("Not found");
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        long uid = uid(req);
        switch (path) {
            case "/save": {
                Address a = new Address();
                a.setId(Web.longParam(req, "id", 0));
                a.setLabel(Web.param(req, "label")); a.setRecipientName(Web.param(req, "recipientName")); a.setPhone(Web.param(req, "phone"));
                a.setLine1(Web.param(req, "line1")); a.setLine2(Web.param(req, "line2")); a.setCity(Web.param(req, "city"));
                a.setState(Web.param(req, "state")); a.setPostalCode(Web.param(req, "postalCode")); a.setCountry(Web.param(req, "country"));
                a.setDefaultAddress("on".equals(req.getParameter("defaultAddress")));
                String next = Web.safeNext(req, req.getParameter("next"), "");
                try {
                    addresses.save(uid, a);
                    flashAndRedirect(req, resp, a.getId() == 0 ? "Address saved." : "Address updated.", next.isEmpty() ? "/account/addresses" : next);
                } catch (FormException e) {
                    req.setAttribute("errors", e.getErrors());
                    req.setAttribute("address", a);
                    req.setAttribute("next", next);
                    Web.view(req, resp, "address/form");
                }
                break;
            }
            case "/delete":
                addresses.delete(uid, Web.longParam(req, "id", 0));
                flashAndRedirect(req, resp, "Address removed.", "/account/addresses");
                break;
            case "/default":
                addresses.setDefault(uid, Web.longParam(req, "id", 0));
                flashAndRedirect(req, resp, "Default address updated.", "/account/addresses");
                break;
            default:
                throw new NotFoundException("Not found");
        }
    }
}
