package com.glamoraa.service;

import com.glamoraa.util.BusinessException;

/**
 * Payment methods. Only Cash on Delivery is implemented - it needs no gateway.
 * An online gateway (e.g. Razorpay TEST mode) would be added here: create the gateway order on the server,
 * verify the signature on the server, and only then mark the order PAID. It is NOT implemented in this
 * version, so nothing here ever pretends a payment succeeded.
 */
public class PaymentService {
    public static final String COD = "COD";

    public String validateMethod(String method) {
        if (COD.equals(method)) return COD;
        throw new BusinessException("Choose a valid payment method.");
    }

    /** Cash on Delivery is collected at the door, so a new order is always PENDING. */
    public String initialPaymentStatus(String method) { return "PENDING"; }
}
