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
import com.example.deephire_android.RetrofitClient.jobRetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    TextView tvEmail;
    Toolbar toolbar;
    RecyclerView rvJobs;
    ProgressBar progressBar;

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
        rvJobs = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar); // Ensure this ID exists in R.layout.activity_user

        rvJobs.setLayoutManager(new LinearLayoutManager(this));

        fetchJobs();
    }

    private void fetchJobs() {
        progressBar.setVisibility(View.VISIBLE);
        JobApiService jobApiService = jobRetrofitClient.getJobApiService();
        Call<List<Job>> call = jobApiService.getJobs("us", 1); // Pass locality=us, start=1

        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    JobRecyclerViewAdapter adapter = new JobRecyclerViewAdapter(response.body(), UserActivity.this);
                    rvJobs.setAdapter(adapter);
                } else {
                    Log.e("API_ERROR", "Response code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(UserActivity.this, "Erreur API jobs: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "Jobs error: " + t.getMessage(), t);
                Toast.makeText(UserActivity.this, "Échec de la connexion (jobs): " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            return true;
        } else if (id == R.id.menu_logout) {
            Log.d("UserActivity", "Logout selected");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}