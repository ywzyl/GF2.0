package com.dje.pageLocator;

public class Locator {
	String by;
    String value;
    String desc;
    //getter setter
    public String getBy() {
        return by;
    }
    public void setBy(String by) {
        this.by = by;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    //construction
    public Locator(String by, String value, String desc) {
        super();
        this.by = by;
        this.value = value;
        this.desc = desc;
    }
    
    //toString
    @Override
    public String toString() {
        return "Locator [by=" + by + ", value=" + value + ", desc=" + desc + "]";
    }
}
