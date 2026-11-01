package com.glamoraa.controller;

import com.glamoraa.util.Web;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Friendly error page. Stack traces and SQL never reach the browser (they are in the server log). */
@WebServlet(name = "ErrorServlet", urlPatterns = "/error")
public class ErrorServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer code = (Integer) req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = code == null ? 500 : code;
        String title, message;
        switch (status) {
            case 400: title = "That request did not look right"; message = "Please go back and try again."; break;
            case 403: title = "You do not have access to this page"; message = "If you were filling in a form, your session may have expired. Go back, refresh the page and try again."; break;
            case 404: title = "We could not find that page"; message = "The link may be old, or the item may no longer be available."; break;
            case 405: title = "That action is not supported"; message = "Please use the buttons on the page instead."; break;
            default: status = 500; title = "Something went wrong on our side"; message = "We have logged the problem. Please try again in a moment."; break;
        }
        resp.setStatus(status);
        req.setAttribute("errorStatus", status);
        req.setAttribute("errorTitle", title);
        req.setAttribute("errorMessage", message);
        Web.view(req, resp, "error");
    }
}
