package com.example.deephire_android.Models;


public class Job {
    private String id;
    private String title;
    private Location location;
    private String formatted_relative_time;
    private String link;
    private String locality;

    // Default constructor for Gson
    public Job() {}

    // Constructor for mapping Adzuna data
    public Job(String id, String title, Location location, String formatted_relative_time, String link, String locality) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.formatted_relative_time = formatted_relative_time;
        this.link = link;
        this.locality = locality;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }

    public String getLocationDisplayName() {
        return location != null ? location.getDisplay_name() : null;
    }

    public String getFormattedRelativeTime() {
        return formatted_relative_time;
    }

    public String getLink() {
        return link;
    }

    public String getLocality() {
        return locality;
         }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setFormattedRelativeTime(String formatted_relative_time) {
        this.formatted_relative_time = formatted_relative_time;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}