package com.glamoraa.model;

/** JavaBean for the ProductVariant entity. */
public class ProductVariant implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long productId;
    private long sizeId;
    private long colorId;
    private String sizeLabel;
    private String colorName;
    private String colorHex;
    private String sku;
    private int stockQuantity;
    private boolean active;
    private String productName;

    public ProductVariant() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public long getSizeId() { return sizeId; }
    public void setSizeId(long sizeId) { this.sizeId = sizeId; }
    public long getColorId() { return colorId; }
    public void setColorId(long colorId) { this.colorId = colorId; }
    public String getSizeLabel() { return sizeLabel; }
    public void setSizeLabel(String sizeLabel) { this.sizeLabel = sizeLabel; }
    public String getColorName() { return colorName; }
    public void setColorName(String colorName) { this.colorName = colorName; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public boolean isInStock() { return active && stockQuantity > 0; }
}
