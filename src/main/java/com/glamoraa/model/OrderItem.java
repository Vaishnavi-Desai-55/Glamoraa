package com.glamoraa.model;

/** JavaBean for the OrderItem entity. */
public class OrderItem implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long orderId;
    private Long variantId;
    private Long productId;
    private String productName;
    private String sku;
    private String sizeLabel;
    private String colorName;
    private String imagePath;
    private java.math.BigDecimal unitPrice;
    private int quantity;
    private java.math.BigDecimal lineTotal;

    public OrderItem() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getSizeLabel() { return sizeLabel; }
    public void setSizeLabel(String sizeLabel) { this.sizeLabel = sizeLabel; }
    public String getColorName() { return colorName; }
    public void setColorName(String colorName) { this.colorName = colorName; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public java.math.BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public java.math.BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(java.math.BigDecimal lineTotal) { this.lineTotal = lineTotal; }

}
