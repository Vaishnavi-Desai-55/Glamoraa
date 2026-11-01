package com.glamoraa.model;

/** JavaBean for the Order entity. */
public class Order implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String orderNumber;
    private long userId;
    private String customerName;
    private String customerEmail;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private java.math.BigDecimal subtotal;
    private java.math.BigDecimal shippingFee;
    private java.math.BigDecimal total;
    private String shipName;
    private String shipPhone;
    private String shipLine1;
    private String shipLine2;
    private String shipCity;
    private String shipState;
    private String shipPostal;
    private String shipCountry;
    private String idempotencyKey;
    private boolean stockRestored;
    private java.util.Date placedAt;
    private int itemCount;

    public Order() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public java.math.BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(java.math.BigDecimal subtotal) { this.subtotal = subtotal; }
    public java.math.BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(java.math.BigDecimal shippingFee) { this.shippingFee = shippingFee; }
    public java.math.BigDecimal getTotal() { return total; }
    public void setTotal(java.math.BigDecimal total) { this.total = total; }
    public String getShipName() { return shipName; }
    public void setShipName(String shipName) { this.shipName = shipName; }
    public String getShipPhone() { return shipPhone; }
    public void setShipPhone(String shipPhone) { this.shipPhone = shipPhone; }
    public String getShipLine1() { return shipLine1; }
    public void setShipLine1(String shipLine1) { this.shipLine1 = shipLine1; }
    public String getShipLine2() { return shipLine2; }
    public void setShipLine2(String shipLine2) { this.shipLine2 = shipLine2; }
    public String getShipCity() { return shipCity; }
    public void setShipCity(String shipCity) { this.shipCity = shipCity; }
    public String getShipState() { return shipState; }
    public void setShipState(String shipState) { this.shipState = shipState; }
    public String getShipPostal() { return shipPostal; }
    public void setShipPostal(String shipPostal) { this.shipPostal = shipPostal; }
    public String getShipCountry() { return shipCountry; }
    public void setShipCountry(String shipCountry) { this.shipCountry = shipCountry; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public boolean isStockRestored() { return stockRestored; }
    public void setStockRestored(boolean stockRestored) { this.stockRestored = stockRestored; }
    public java.util.Date getPlacedAt() { return placedAt; }
    public void setPlacedAt(java.util.Date placedAt) { this.placedAt = placedAt; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    private java.util.List<OrderItem> items = new java.util.ArrayList<>();
    public java.util.List<OrderItem> getItems() { return items; }
    public void setItems(java.util.List<OrderItem> items) { this.items = items; }
    public boolean isCancellable() { return "PLACED".equals(status) || "CONFIRMED".equals(status); }
    /** Next statuses an admin may move this order to. */
    public java.util.List<String> getNextStatuses() {
        switch (status) {
            case "PLACED": return java.util.List.of("CONFIRMED", "CANCELLED");
            case "CONFIRMED": return java.util.List.of("SHIPPED", "CANCELLED");
            case "SHIPPED": return java.util.List.of("DELIVERED");
            default: return java.util.List.of();
        }
    }
}
