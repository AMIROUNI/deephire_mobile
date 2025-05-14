package com.example.deephire_android.RetrofitClient;

import com.example.deephire_android.ApiService.JobApiService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class jobRetrofitClient {
    private static final String BASE_URL = "https://indeed12.p.rapidapi.com/";
    private static final String API_KEY = "c9567bd0admshfb13a9906493331p110638jsn3e767c3ce4a6";
    private static final String API_HOST = "indeed12.p.rapidapi.com";

    private static Retrofit retrofit;

    public static JobApiService getJobApiService() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("x-rapidapi-key", API_KEY)
                                .addHeader("x-rapidapi-host", API_HOST)
                                .build();
                        return chain.proceed(newRequest);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(JobApiService.class);
    }
}