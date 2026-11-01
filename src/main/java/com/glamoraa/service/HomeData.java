package com.glamoraa.service;

import com.glamoraa.model.Category;
import com.glamoraa.model.Product;
import java.util.List;

public class HomeData {
    private List<Category> categories;
    private List<Product> newArrivals;
    private List<Product> featured;
    private List<Product> bestSellers;

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> v) { this.categories = v; }
    public List<Product> getNewArrivals() { return newArrivals; }
    public void setNewArrivals(List<Product> v) { this.newArrivals = v; }
    public List<Product> getFeatured() { return featured; }
    public void setFeatured(List<Product> v) { this.featured = v; }
    public List<Product> getBestSellers() { return bestSellers; }
    public void setBestSellers(List<Product> v) { this.bestSellers = v; }
}
