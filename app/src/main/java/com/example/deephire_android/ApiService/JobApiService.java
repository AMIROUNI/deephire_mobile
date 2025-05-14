package com.example.deephire_android.ApiService;

import com.example.deephire_android.Models.Job;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JobApiService {
    @GET("company/Ubisoft/jobs")
    Call<List<Job>> getJobs(@Query("locality") String locality, @Query("start") int start);
}