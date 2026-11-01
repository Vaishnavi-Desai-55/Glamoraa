package com.glamoraa.controller;

import com.glamoraa.model.Product;
import com.glamoraa.service.AdminService;
import com.glamoraa.util.AppConfig;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Product image management: upload (validated by file signature, not by file name), attach an existing site image path,
 * make primary, delete. Uploaded files are stored outside the WAR and served by MediaServlet.
 */
@WebServlet(name = "AdminImageServlet", urlPatterns = "/admin/images")
@MultipartConfig(maxFileSize = 3 * 1024 * 1024, maxRequestSize = 4 * 1024 * 1024)
public class AdminImageServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdminService admin = new AdminService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long productId = Web.longParam(req, "productId", 0);
        Product p = admin.product(productId);
        req.setAttribute("p", p);
        req.setAttribute("images", admin.images(productId));
        Web.view(req, resp, "admin/images");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long productId = Web.longParam(req, "productId", 0);
        String action = Web.param(req, "action");
        String alt = Web.param(req, "alt");
        if (alt.length() > 200) alt = alt.substring(0, 200);
        String message;
        switch (action) {
            case "upload": {
                Part part = req.getPart("file");
                if (part == null || part.getSize() == 0) throw new BusinessException("Choose an image file to upload.");
                long max = AppConfig.getInt("upload.max_bytes", 3 * 1024 * 1024);
                if (part.getSize() > max) throw new BusinessException("That image is larger than " + (max / 1024 / 1024) + " MB.");
                byte[] head = new byte[12];
                int n;
                try (InputStream in = part.getInputStream()) { n = in.readNBytes(head, 0, 12); }
                String ext = detectExtension(head, n);
                if (ext == null) throw new BusinessException("Only JPG, PNG, WebP or GIF images can be uploaded.");
                Path dir = MediaServlet.uploadDir();
                Files.createDirectories(dir);
                String name = "p" + productId + "-" + UUID.randomUUID().toString().replace("-", "") + "." + ext;
                try (InputStream in = part.getInputStream()) { Files.copy(in, dir.resolve(name)); }
                admin.addImage(productId, "/media/" + name, alt.isEmpty() ? null : alt);
                message = "Image uploaded.";
                break;
            }
            case "path": {
                String path = Web.param(req, "imagePath");
                if (!path.matches("^/(assets/images|media)/[A-Za-z0-9_./-]+$") || path.contains("..")) throw new BusinessException("Use a site path such as /assets/images/products/example.svg.");
                admin.addImage(productId, path, alt.isEmpty() ? null : alt);
                message = "Image added.";
                break;
            }
            case "primary":
                admin.makePrimary(productId, Web.longParam(req, "imageId", 0));
                message = "Primary image updated.";
                break;
            case "delete": {
                String removed = admin.deleteImage(productId, Web.longParam(req, "imageId", 0));
                if (removed != null && removed.startsWith("/media/")) Files.deleteIfExists(MediaServlet.uploadDir().resolve(removed.substring(7)));
                message = "Image removed.";
                break;
            }
            default:
                throw new BusinessException("Unknown action.");
        }
        flashAndRedirect(req, resp, message, "/admin/images?productId=" + productId);
    }

    private static String detectExtension(byte[] b, int n) {
        if (n >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) return "jpg";
        if (n >= 4 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') return "png";
        if (n >= 4 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8') return "gif";
        if (n >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F' && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') return "webp";
        return null;
    }
}
