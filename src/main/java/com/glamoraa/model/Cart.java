package com.glamoraa.model;

/** JavaBean for the Cart entity. */
public class Cart implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long userId;

    public Cart() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    private java.util.List<CartItem> items = new java.util.ArrayList<>();
    public java.util.List<CartItem> getItems() { return items; }
    public void setItems(java.util.List<CartItem> items) { this.items = items; }
    public int getItemCount() { int n = 0; for (CartItem i : items) n += i.getQuantity(); return n; }
    public boolean isEmpty() { return items.isEmpty(); }
    public java.math.BigDecimal getSubtotal() {
        java.math.BigDecimal t = java.math.BigDecimal.ZERO;
        for (CartItem i : items) t = t.add(i.getLineTotal());
        return t;
    }
    /** True when every line can currently be bought in the requested quantity. */
    public boolean isCheckoutReady() {
        if (items.isEmpty()) return false;
        for (CartItem i : items) if (!i.isAvailable() || i.getQuantity() > i.getStock()) return false;
        return true;
    }
}
