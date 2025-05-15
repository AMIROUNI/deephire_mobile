package com.example.deephire_android.RetrofitClient;

import com.example.deephire_android.ApiService.JobApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class jobRetrofitClient {
    private static final String BASE_URL = "https://api.adzuna.com/";
    private static Retrofit retrofit;

    public static JobApiService getJobApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(JobApiService.class);
    }
}