package com.example.deephire_android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deephire_android.Adapters.JobRecyclerViewAdapter;
import com.example.deephire_android.ApiService.JobApiService;
import com.example.deephire_android.Models.Job;
import com.example.deephire_android.R;
import com.example.deephire_android.Response.JobSearchResponse;
import com.example.deephire_android.RetrofitClient.jobRetrofitClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    TextView tvEmail;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView errorText;

    private static final String APP_ID = "4db73112";
    private static final String APP_KEY = "5da30487d33185df99c6c348619d628e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Email
        tvEmail = findViewById(R.id.chipProfile);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        Log.d("UserActivity", "Received email: " + email);
        if (email != null) {
            tvEmail.setText(email);
        } else {
            Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show();
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchJobs();
    }

    private void fetchJobs() {
        // Show ProgressBar if available
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        // Hide errorText if available
        if (errorText != null) {
            errorText.setVisibility(View.GONE);
        }

        JobApiService jobApiService = jobRetrofitClient.getJobApiService();
        Call<JobSearchResponse> call = jobApiService.getJobs(APP_ID, APP_KEY);

        call.enqueue(new Callback<JobSearchResponse>() {
            @Override
            public void onResponse(Call<JobSearchResponse> call, Response<JobSearchResponse> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    // Map Adzuna jobs to your Job model
                    List<Job> jobs = new ArrayList<>();
                    for (Job adzunaJob : response.body().getResults()) {
                        String relativeTime = convertToRelativeTime(adzunaJob.getFormattedRelativeTime());
                        Job job = new Job(
                                adzunaJob.getId(),
                                adzunaJob.getTitle(),
                                adzunaJob.getLocation(),
                                relativeTime,
                                adzunaJob.getLink(),
                                adzunaJob.getLocality()
                        );
                        jobs.add(job);
                    }
                    JobRecyclerViewAdapter adapter = new JobRecyclerViewAdapter(jobs, UserActivity.this);
                    recyclerView.setAdapter(adapter);
                    Log.d("API_SUCCESS", "Jobs loaded: " + jobs.size());
                } else {
                    StringBuilder errorDetails = new StringBuilder();
                    errorDetails.append("Response code: ").append(response.code())
                            .append(", Message: ").append(response.message());
                    if (response.errorBody() != null) {
                        try {
                            errorDetails.append(", Error Body: ").append(response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("API_ERROR", "Error parsing errorBody: " + e.getMessage());
                        }
                    }
                    Log.e("API_ERROR", errorDetails.toString());
                    String toastMessage = "Failed to load jobs (Code: " + response.code() + ")";
                    String errorTextMessage = "Failed to load jobs (Code: " + response.code() + ")";
                    if (response.code() == 403) {
                        toastMessage = "API access denied. Check your app ID and key.";
                        errorTextMessage = "API access denied.";
                    }
                    Toast.makeText(UserActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                    if (errorText != null) {
                        errorText.setText(errorTextMessage);
                        errorText.setVisibility(View.VISIBLE);
                    }

                    // Fallback to mock data
                    List<Job> mockJobs = new ArrayList<>();
                    for (int i = 1; i <= 5; i++) {
                        mockJobs.add(new Job(
                                "mock_job_" + i,
                                "Mock Job " + i,
                                "Mock City " + i,
                                i + " days ago",
                                "https://example.com/mock_job_" + i,
                                "us"
                        ));
                    }
                    JobRecyclerViewAdapter adapter = new JobRecyclerViewAdapter(mockJobs, UserActivity.this);
                    recyclerView.setAdapter(adapter);
                    Log.d("API_ERROR", "Using mock data");
                }
            }

            @Override
            public void onFailure(Call<JobSearchResponse> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Log.e("API_ERROR", "Network error: " + t.getMessage(), t);
                Toast.makeText(UserActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                if (errorText != null) {
                    errorText.setText("Network error: " + t.getMessage());
                    errorText.setVisibility(View.VISIBLE);
                }

                // Fallback to mock data
                List<Job> mockJobs = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    mockJobs.add(new Job(
                            "mock_job_" + i,
                            "Mock Job " + i,
                            "Mock City " + i,
                            i + " days ago",
                            "https://example.com/mock_job_" + i,
                            "us"
                    ));
                }
                JobRecyclerViewAdapter adapter = new JobRecyclerViewAdapter(mockJobs, UserActivity.this);
                recyclerView.setAdapter(adapter);
                Log.d("API_ERROR", "Using mock data");
            }
        });
    }

    private String convertToRelativeTime(String created) {
        if (created == null || created.isEmpty()) {
            return "Unknown time";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            Date date = sdf.parse(created);
            if (date == null) {
                return "Unknown time";
            }
            long diffMillis = System.currentTimeMillis() - date.getTime();
            long days = diffMillis / (1000 * 60 * 60 * 24);
            if (days == 0) {
                return "Today";
            } else if (days == 1) {
                return "1 day ago";
            } else {
                return days + " days ago";
            }
        } catch (ParseException e) {
            Log.e("DATE_PARSE", "Failed to parse date: " + created, e);
            return "Unknown time";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_profile) {
            Log.d("UserActivity", "Profile menu selected");
            return true;
        } else if (id == R.id.menu_logout) {
            Log.d("UserActivity", "Logout selected, finishing activity");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Log.d("UserActivity", "finish() called", new Throwable("Stack trace"));
        super.finish();
    }

    @Override
    public void onBackPressed() {
        Log.d("UserActivity", "Back button pressed");
        super.onBackPressed();
    }
}