package com.glamoraa.model;

/** JavaBean for the Size entity. */
public class Size implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String label;
    private int sortOrder;

    public Size() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

}
