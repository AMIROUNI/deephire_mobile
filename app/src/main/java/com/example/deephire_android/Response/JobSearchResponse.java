package com.example.deephire_android.Response;


import com.example.deephire_android.Models.Job;

import java.util.List;

public class JobSearchResponse {
    private List<Job> results;

    public List<Job> getResults() {
        return results;
    }

    public void setResults(List<Job> results) {
        this.results = results;
    }
}