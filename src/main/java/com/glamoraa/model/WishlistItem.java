package com.glamoraa.model;

/** JavaBean for the WishlistItem entity. */
public class WishlistItem implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long userId;
    private long productId;
    private java.util.Date createdAt;

    public WishlistItem() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public java.util.Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.util.Date createdAt) { this.createdAt = createdAt; }

}
