package com.glamoraa.model;

/** JavaBean for the Product entity. */
public class Product implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long categoryId;
    private String categoryName;
    private String categorySlug;
    private String name;
    private String slug;
    private String description;
    private String specifications;
    private java.math.BigDecimal price;
    private int discountPercent;
    private java.math.BigDecimal salePrice;
    private boolean featured;
    private boolean newArrival;
    private boolean active;
    private java.util.Date createdAt;
    private String primaryImage;
    private String secondImage;
    private double avgRating;
    private int reviewCount;
    private int totalStock;

    public Product() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategorySlug() { return categorySlug; }
    public void setCategorySlug(String categorySlug) { this.categorySlug = categorySlug; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }
    public java.math.BigDecimal getPrice() { return price; }
    public void setPrice(java.math.BigDecimal price) { this.price = price; }
    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
    public java.math.BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(java.math.BigDecimal salePrice) { this.salePrice = salePrice; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public boolean isNewArrival() { return newArrival; }
    public void setNewArrival(boolean newArrival) { this.newArrival = newArrival; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public java.util.Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.util.Date createdAt) { this.createdAt = createdAt; }
    public String getPrimaryImage() { return primaryImage; }
    public void setPrimaryImage(String primaryImage) { this.primaryImage = primaryImage; }
    public String getSecondImage() { return secondImage; }
    public void setSecondImage(String secondImage) { this.secondImage = secondImage; }
    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public int getTotalStock() { return totalStock; }
    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }

    public boolean isHasDiscount() { return discountPercent > 0; }
    public boolean isInStock() { return totalStock > 0; }
    public boolean isHasReviews() { return reviewCount > 0; }
    /** Specification lines ("Label: value") split into [label, value] pairs. */
    public java.util.List<String[]> getSpecList() {
        java.util.List<String[]> out = new java.util.ArrayList<>();
        if (specifications == null) return out;
        for (String line : specifications.split("\\r?\\n")) {
            if (line.isBlank()) continue;
            int i = line.indexOf(':');
            out.add(i > 0 ? new String[]{line.substring(0, i).trim(), line.substring(i + 1).trim()} : new String[]{"", line.trim()});
        }
        return out;
    }
}
