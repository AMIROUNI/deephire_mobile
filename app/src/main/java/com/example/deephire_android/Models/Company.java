package com.example.deephire_android.Models;

import java.util.List;

public class Company {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String website;
    private String industry;
    private String location; // New field
    private String description;
    private String logoUrl;
    private List<String> jobPostings;

    public Company() {
    }

    public Company(String id, String name, String email, String phone, String website,
                   String industry, String location, String description,
                   String logoUrl, List<String> jobPostings) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.industry = industry;
        this.location = location;
        this.description = description;
        this.logoUrl = logoUrl;
        this.jobPostings = jobPostings;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<String> getJobPostings() {
        return jobPostings;
    }

    public void setJobPostings(List<String> jobPostings) {
        this.jobPostings = jobPostings;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}