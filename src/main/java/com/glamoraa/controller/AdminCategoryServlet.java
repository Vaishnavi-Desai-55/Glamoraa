package com.glamoraa.controller;

import com.glamoraa.model.Category;
import com.glamoraa.service.AdminService;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.FormException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminCategoryServlet", urlPatterns = "/admin/categories")
public class AdminCategoryServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdminService admin = new AdminService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long editId = Web.longParam(req, "edit", 0);
        Category editing = new Category();
        editing.setActive(true);
        editing.setSortOrder(10);
        if (editId > 0) editing = admin.allCategories().stream().filter(c -> c.getId() == editId).findFirst().orElse(editing);
        show(req, resp, editing);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = Web.param(req, "action");
        if ("delete".equals(action)) {
            admin.deleteCategory(Web.longParam(req, "id", 0));
            flashAndRedirect(req, resp, "Category deleted.", "/admin/categories");
            return;
        }
        if (!"save".equals(action)) throw new BusinessException("Unknown action.");
        Category c = new Category();
        c.setId(Web.longParam(req, "id", 0));
        c.setName(Web.param(req, "name")); c.setSubtitle(Web.param(req, "subtitle")); c.setDescription(Web.param(req, "description"));
        c.setImagePath(Web.param(req, "imagePath")); c.setSortOrder(Web.intParam(req, "sortOrder", 0));
        c.setActive(req.getParameter("active") != null);
        try {
            admin.saveCategory(c);
            flashAndRedirect(req, resp, "Category saved.", "/admin/categories");
        } catch (FormException e) {
            req.setAttribute("errors", e.getErrors());
            show(req, resp, c);
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, Category editing) throws ServletException, IOException {
        req.setAttribute("categories", admin.allCategories());
        req.setAttribute("editing", editing);
        Web.view(req, resp, "admin/categories");
    }
}
