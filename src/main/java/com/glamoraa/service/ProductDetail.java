package com.glamoraa.service;

import com.glamoraa.model.Color;
import com.glamoraa.model.Product;
import com.glamoraa.model.ProductImage;
import com.glamoraa.model.ProductVariant;
import com.glamoraa.model.Review;
import com.glamoraa.model.Size;
import java.util.List;

/** Everything the product page needs, loaded in one service call. */
public class ProductDetail {
    private Product product;
    private List<ProductImage> images;
    private List<ProductVariant> variants;
    private List<Size> sizes;
    private List<Color> colors;
    private List<Product> related;
    private List<Review> reviews;
    private boolean canReview;

    public Product getProduct() { return product; }
    public void setProduct(Product v) { this.product = v; }
    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> v) { this.images = v; }
    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> v) { this.variants = v; }
    public List<Size> getSizes() { return sizes; }
    public void setSizes(List<Size> v) { this.sizes = v; }
    public List<Color> getColors() { return colors; }
    public void setColors(List<Color> v) { this.colors = v; }
    public List<Product> getRelated() { return related; }
    public void setRelated(List<Product> v) { this.related = v; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> v) { this.reviews = v; }
    public boolean isCanReview() { return canReview; }
    public void setCanReview(boolean v) { this.canReview = v; }
}
