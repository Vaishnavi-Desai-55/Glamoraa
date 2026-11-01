package com.glamoraa.service;

import com.glamoraa.util.AppConfig;
import java.math.BigDecimal;

/** The single place where shipping rules live. Used by the cart page, checkout and order creation. */
public class PricingService {
    public BigDecimal freeShippingThreshold() { return AppConfig.getMoney("shipping.free_threshold", new BigDecimal("2999")); }
    public BigDecimal flatShippingFee() { return AppConfig.getMoney("shipping.flat_fee", new BigDecimal("99")); }

    public BigDecimal shippingFor(BigDecimal subtotal) {
        if (subtotal.signum() == 0) return BigDecimal.ZERO;
        return subtotal.compareTo(freeShippingThreshold()) >= 0 ? BigDecimal.ZERO : flatShippingFee();
    }
}
