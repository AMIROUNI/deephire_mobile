package com.example.deephire_android.Models;

import java.util.List;

public class Location {
    private String display_name;
    private List<String> area;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }
}