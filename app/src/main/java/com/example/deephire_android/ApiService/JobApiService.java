package com.example.deephire_android.ApiService;

import com.example.deephire_android.Models.Job;
import com.example.deephire_android.Response.JobSearchResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JobApiService {
    @GET("v1/api/jobs/gb/search/1")
    Call<JobSearchResponse> getJobs(
            @Query("app_id") String appId,
            @Query("app_key") String appKey
    );
}