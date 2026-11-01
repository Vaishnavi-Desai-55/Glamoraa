package com.glamoraa.service;

import com.glamoraa.model.Address;
import com.glamoraa.model.Cart;
import java.math.BigDecimal;
import java.util.List;

public class CheckoutSummary {
    private Cart cart;
    private List<Address> addresses;
    private BigDecimal shipping;
    private BigDecimal total;

    public Cart getCart() { return cart; }
    public void setCart(Cart v) { this.cart = v; }
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> v) { this.addresses = v; }
    public BigDecimal getShipping() { return shipping; }
    public void setShipping(BigDecimal v) { this.shipping = v; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal v) { this.total = v; }
    public BigDecimal getSubtotal() { return cart.getSubtotal(); }
}
