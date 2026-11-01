package com.glamoraa.model;

/** JavaBean for the CartItem entity. */
public class CartItem implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long cartId;
    private long variantId;
    private int quantity;
    private long productId;
    private String productName;
    private String sizeLabel;
    private String colorName;
    private String sku;
    private java.math.BigDecimal unitPrice;
    private java.math.BigDecimal originalPrice;
    private int stock;
    private String imagePath;
    private boolean available;

    public CartItem() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCartId() { return cartId; }
    public void setCartId(long cartId) { this.cartId = cartId; }
    public long getVariantId() { return variantId; }
    public void setVariantId(long variantId) { this.variantId = variantId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSizeLabel() { return sizeLabel; }
    public void setSizeLabel(String sizeLabel) { this.sizeLabel = sizeLabel; }
    public String getColorName() { return colorName; }
    public void setColorName(String colorName) { this.colorName = colorName; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public java.math.BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public java.math.BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(java.math.BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public java.math.BigDecimal getLineTotal() { return unitPrice.multiply(java.math.BigDecimal.valueOf(quantity)); }
    public boolean isShortStock() { return available && quantity > stock; }
}
