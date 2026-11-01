package com.glamoraa.service;

import com.glamoraa.dao.AddressDAO;
import com.glamoraa.dao.CartDAO;
import com.glamoraa.dao.OrderDAO;
import com.glamoraa.dao.ProductVariantDAO;
import com.glamoraa.dao.impl.AddressDAOImpl;
import com.glamoraa.dao.impl.CartDAOImpl;
import com.glamoraa.dao.impl.OrderDAOImpl;
import com.glamoraa.dao.impl.ProductVariantDAOImpl;
import com.glamoraa.model.Address;
import com.glamoraa.model.Cart;
import com.glamoraa.model.CartItem;
import com.glamoraa.model.Order;
import com.glamoraa.model.OrderItem;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.Tx;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class CheckoutService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final CartDAO carts = new CartDAOImpl();
    private final AddressDAO addresses = new AddressDAOImpl();
    private final OrderDAO orders = new OrderDAOImpl();
    private final ProductVariantDAO variants = new ProductVariantDAOImpl();
    private final PricingService pricing = new PricingService();
    private final PaymentService payments = new PaymentService();

    public CheckoutSummary prepare(long userId) {
        return Tx.read(c -> {
            Cart cart = carts.findOrCreate(c, userId);
            cart.setItems(carts.items(c, cart.getId()));
            CheckoutSummary s = new CheckoutSummary();
            s.setCart(cart);
            s.setAddresses(addresses.findByUser(c, userId));
            s.setShipping(pricing.shippingFor(cart.getSubtotal()));
            s.setTotal(cart.getSubtotal().add(s.getShipping()));
            return s;
        });
    }

    /**
     * Places an order in ONE transaction:
     *  1. lock the variant rows (always in id order, so two shoppers can't deadlock),
     *  2. re-read prices and stock under the lock - nothing from the browser is trusted,
     *  3. decrement stock with a conditional UPDATE (cannot go below zero),
     *  4. write the order + snapshot lines, then empty the cart.
     * Any failure rolls everything back.
     */
    public Order placeOrder(long userId, long addressId, String paymentMethod, String idempotencyKey) {
        final String method = payments.validateMethod(paymentMethod);
        if (idempotencyKey == null || idempotencyKey.length() != 36) throw new BusinessException("This checkout session expired. Please review your bag and try again.");
        try {
            return Tx.write(c -> {
                Address addr = addresses.findForUser(c, addressId, userId);
                if (addr == null) throw new BusinessException("Choose a delivery address to continue.");
                Cart cart = carts.findOrCreate(c, userId);
                List<CartItem> first = carts.items(c, cart.getId());
                if (first.isEmpty()) throw new BusinessException("Your bag is empty.");

                first.stream().map(CartItem::getVariantId).sorted().forEach(id -> {
                    try { variants.lockById(c, id); } catch (java.sql.SQLException e) { throw new IllegalStateException(e); }
                });
                List<CartItem> items = carts.items(c, cart.getId());   // fresh prices + stock, now protected by the locks
                items.sort(Comparator.comparingLong(CartItem::getVariantId));

                BigDecimal subtotal = BigDecimal.ZERO;
                for (CartItem i : items) {
                    if (!i.isAvailable()) throw new BusinessException(i.getProductName() + " is no longer available. Remove it from your bag to continue.");
                    if (i.getQuantity() > i.getStock()) throw new BusinessException("Only " + i.getStock() + " of " + i.getProductName() + " (" + i.getSizeLabel() + ", " + i.getColorName() + ") left. Update your bag to continue.");
                    if (variants.decrementStock(c, i.getVariantId(), i.getQuantity()) != 1) throw new BusinessException(i.getProductName() + " just sold out. Please update your bag.");
                    subtotal = subtotal.add(i.getLineTotal());
                }
                BigDecimal shipping = pricing.shippingFor(subtotal);

                Order o = new Order();
                o.setOrderNumber(newOrderNumber());
                o.setUserId(userId);
                o.setStatus("PLACED");
                o.setPaymentMethod(method);
                o.setPaymentStatus(payments.initialPaymentStatus(method));
                o.setSubtotal(subtotal);
                o.setShippingFee(shipping);
                o.setTotal(subtotal.add(shipping));
                o.setShipName(addr.getRecipientName()); o.setShipPhone(addr.getPhone());
                o.setShipLine1(addr.getLine1()); o.setShipLine2(addr.getLine2());
                o.setShipCity(addr.getCity()); o.setShipState(addr.getState());
                o.setShipPostal(addr.getPostalCode()); o.setShipCountry(addr.getCountry());
                o.setIdempotencyKey(idempotencyKey);
                o.setId(orders.insert(c, o));

                for (CartItem i : items) {
                    OrderItem oi = new OrderItem();
                    oi.setOrderId(o.getId()); oi.setVariantId(i.getVariantId()); oi.setProductId(i.getProductId());
                    oi.setProductName(i.getProductName()); oi.setSku(i.getSku()); oi.setSizeLabel(i.getSizeLabel());
                    oi.setColorName(i.getColorName()); oi.setImagePath(i.getImagePath());
                    oi.setUnitPrice(i.getUnitPrice()); oi.setQuantity(i.getQuantity()); oi.setLineTotal(i.getLineTotal());
                    orders.insertItem(c, oi);
                }
                carts.clear(c, cart.getId());       // only after the order exists, inside the same transaction
                return o;
            });
        } catch (IllegalStateException e) {
            throw new com.glamoraa.util.DataAccessException("Could not lock stock", e.getCause());
        } catch (com.glamoraa.util.DataAccessException e) {
            if (Tx.isDuplicate(e)) throw new BusinessException("This order was already submitted. Check your order history.");
            throw e;
        }
    }

    private static String newOrderNumber() {
        StringBuilder sb = new StringBuilder("GL").append(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).append('-');
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        for (int i = 0; i < 6; i++) sb.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        return sb.toString();
    }
}
