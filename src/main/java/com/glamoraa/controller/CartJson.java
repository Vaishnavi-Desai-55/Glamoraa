package com.glamoraa.controller;

import com.glamoraa.model.Cart;
import com.glamoraa.model.CartItem;
import com.glamoraa.service.PricingService;
import com.glamoraa.util.Fn;
import com.glamoraa.util.Json;
import com.glamoraa.util.Money;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Serialises the server's cart for the slide-out drawer. The browser only ever displays what the server confirmed. */
final class CartJson {
    private CartJson() { }

    static String of(Cart cart, String ctx, String message) {
        PricingService pricing = new PricingService();
        List<String> items = new ArrayList<>();
        for (CartItem i : cart.getItems()) {
            items.add(Json.obj().put("id", i.getId()).put("name", i.getProductName()).put("size", i.getSizeLabel()).put("color", i.getColorName())
                    .put("quantity", i.getQuantity()).put("unit", Money.format(i.getUnitPrice())).put("line", Money.format(i.getLineTotal()))
                    .put("image", ctx + Fn.img(i.getImagePath())).put("url", ctx + "/product?id=" + i.getProductId())
                    .put("available", i.isAvailable() && i.getQuantity() <= i.getStock()).end());
        }
        BigDecimal shipping = pricing.shippingFor(cart.getSubtotal());
        return Json.obj().put("ok", true).put("message", message == null ? "" : message).put("count", cart.getItemCount())
                .put("subtotal", Money.format(cart.getSubtotal())).put("shipping", shipping.signum() == 0 ? "Free" : Money.format(shipping))
                .put("total", Money.format(cart.getSubtotal().add(shipping))).put("empty", cart.isEmpty())
                .raw("items", "[" + String.join(",", items) + "]").end();
    }
}
