package com.glamoraa.model;

/** JavaBean for the Color entity. */
public class Color implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private String hexCode;

    public Color() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHexCode() { return hexCode; }
    public void setHexCode(String hexCode) { this.hexCode = hexCode; }

}
