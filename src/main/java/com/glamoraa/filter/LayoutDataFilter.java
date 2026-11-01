package com.glamoraa.filter;

import com.glamoraa.model.User;
import com.glamoraa.service.CartService;
import com.glamoraa.service.PricingService;
import com.glamoraa.service.ProductService;
import com.glamoraa.service.WishlistService;
import com.glamoraa.util.AppConfig;
import com.glamoraa.util.Csrf;
import com.glamoraa.util.Money;
import com.glamoraa.util.Web;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Puts the data every page needs (navigation, cart count, flash message, CSRF token...) on the request. */
public class LayoutDataFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(LayoutDataFilter.class.getName());
    private final ProductService products = new ProductService();
    private final CartService carts = new CartService();
    private final WishlistService wishlist = new WishlistService();
    private final PricingService pricing = new PricingService();

    @Override public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        String path = r.getRequestURI().substring(r.getContextPath().length());
        boolean page = ("GET".equals(r.getMethod()) || r.getDispatcherType() == DispatcherType.ERROR)
                && !path.startsWith("/assets/") && !path.startsWith("/media/") && !path.startsWith("/cart/summary");
        if (page) {
            r.setAttribute("ctx", r.getContextPath());
            r.setAttribute("siteName", AppConfig.get("site.name"));
            r.setAttribute("tagline", AppConfig.get("site.tagline"));
            r.setAttribute("announcement", AppConfig.get("site.announcement").replace("{freeShipping}", Money.format(pricing.freeShippingThreshold())));
            r.setAttribute("instagram", AppConfig.get("social.instagram"));
            r.setAttribute("facebook", AppConfig.get("social.facebook"));
            r.setAttribute("pinterest", AppConfig.get("social.pinterest"));
            r.setAttribute("contactEmail", AppConfig.get("contact.email"));
            r.setAttribute("maxQty", CartService.maxPerItem());
            r.setAttribute("freeShippingThreshold", pricing.freeShippingThreshold());
            r.setAttribute("flatShippingFee", pricing.flatShippingFee());
            HttpSession session = r.getSession(true);
            r.setAttribute("csrfToken", Csrf.token(session));
            String[] flash = (String[]) session.getAttribute(Web.FLASH_KEY);
            if (flash != null) { r.setAttribute("flash", flash); session.removeAttribute(Web.FLASH_KEY); }
            User user = (User) session.getAttribute(Web.USER_KEY);
            r.setAttribute("user", user);
            r.setAttribute("navCategories", List.of());
            r.setAttribute("wishIds", new HashSet<Long>());
            r.setAttribute("cartCount", 0);
            try {
                r.setAttribute("navCategories", products.navCategories());
                if (user != null) {
                    r.setAttribute("cartCount", carts.count(user.getId()));
                    r.setAttribute("wishIds", wishlist.ids(user.getId()));
                }
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "Could not load layout data (is the database reachable?)", e);
            }
        }
        chain.doFilter(req, resp);
    }
}
