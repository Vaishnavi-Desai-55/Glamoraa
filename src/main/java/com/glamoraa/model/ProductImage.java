package com.glamoraa.model;

/** JavaBean for the ProductImage entity. */
public class ProductImage implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long productId;
    private String imagePath;
    private String altText;
    private int sortOrder;
    private boolean primary;

    public ProductImage() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }

}
