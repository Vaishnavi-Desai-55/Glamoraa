package com.glamoraa.dao;

import java.math.BigDecimal;

/** Search/sort/paginate criteria for the catalog. Sort keys are mapped through an allowlist in ProductDAOImpl. */
public class ProductFilter {
    public long categoryId;
    public String query;
    public BigDecimal minPrice;
    public BigDecimal maxPrice;
    public boolean featuredOnly;
    public boolean newOnly;
    public boolean onlyActive = true;
    public String sort = "newest";
    public int limit = 12;
    public int offset = 0;
}
