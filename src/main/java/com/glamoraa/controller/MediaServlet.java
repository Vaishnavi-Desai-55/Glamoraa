package com.glamoraa.controller;

import com.glamoraa.util.AppConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/** Serves admin-uploaded product images from the upload folder (outside the WAR, so redeploys keep them). */
@WebServlet(name = "MediaServlet", urlPatterns = "/media/*")
public class MediaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Map<String, String> TYPES = Map.of("jpg", "image/jpeg", "jpeg", "image/jpeg", "png", "image/png", "webp", "image/webp", "gif", "image/gif");

    public static Path uploadDir() {
        String configured = AppConfig.get("upload.dir");
        return configured.isEmpty() ? Paths.get(System.getProperty("user.home"), "glamoraa-uploads") : Paths.get(configured);
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getPathInfo() == null ? "" : req.getPathInfo().substring(1);
        if (!name.matches("[A-Za-z0-9_-]+\\.(jpg|jpeg|png|webp|gif)")) { resp.sendError(404); return; }   // no path traversal possible
        Path file = uploadDir().resolve(name);
        if (!Files.isRegularFile(file)) { resp.sendError(404); return; }
        resp.setContentType(TYPES.get(name.substring(name.lastIndexOf('.') + 1)));
        resp.setContentLengthLong(Files.size(file));
        resp.setHeader("Cache-Control", "public, max-age=86400");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        Files.copy(file, resp.getOutputStream());
    }
}
