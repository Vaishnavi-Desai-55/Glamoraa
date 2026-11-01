package com.glamoraa.controller;

import com.glamoraa.service.WishlistService;
import com.glamoraa.util.Json;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** POST /wishlist/add | remove | toggle | move. The user is always the session user. */
@WebServlet(name = "WishlistActionServlet", urlPatterns = "/wishlist/*")
public class WishlistActionServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final WishlistService wishlist = new WishlistService();

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long uid = uid(req);
        long productId = Web.longParam(req, "productId", 0);
        String action = req.getPathInfo() == null ? "" : req.getPathInfo();
        boolean wished;
        String message;
        switch (action) {
            case "/add": wishlist.add(uid, productId); wished = true; message = "Saved to your wishlist."; break;
            case "/remove": wishlist.remove(uid, productId); wished = false; message = "Removed from your wishlist."; break;
            case "/toggle": wished = wishlist.toggle(uid, productId); message = wished ? "Saved to your wishlist." : "Removed from your wishlist."; break;
            case "/move":
                wishlist.moveToCart(uid, productId, Web.longParam(req, "variantId", 0));
                flashAndRedirect(req, resp, "Moved to your bag.", "/wishlist");
                return;
            default: throw new NotFoundException("Not found");
        }
        if (Web.wantsJson(req)) {
            Web.json(resp, 200, Json.obj().put("ok", true).put("wished", wished).put("count", wishlist.ids(uid).size()).put("message", message).end());
        } else {
            flashAndRedirect(req, resp, message, Web.backPath(req, "/wishlist"));
        }
    }
}
