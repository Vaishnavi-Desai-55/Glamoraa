package com.glamoraa.util;

import java.util.List;

/** One page of results plus the numbers a pagination control needs. */
public class Page<T> {
    private final List<T> items;
    private final int page;
    private final int size;
    private final long total;

    public Page(List<T> items, int page, int size, long total) {
        this.items = items; this.page = page; this.size = size; this.total = total;
    }

    public List<T> getItems() { return items; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotal() { return total; }
    public int getTotalPages() { return (int) Math.max(1, (total + size - 1) / size); }
    public boolean isEmpty() { return items.isEmpty(); }
}
