package com.example.deephire_android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.deephire_android.R;

public class UserActivity extends AppCompatActivity {

    TextView tvEmail;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Make Toolbar act as ActionBar

        // Set email from Intent
        tvEmail = findViewById(R.id.tvEmail);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        if (email != null) {
            tvEmail.setText(email);
        }
    }

    // Inflate the menu into the Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu); // Replace with your menu XML
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            return true;
        } else if (id == R.id.menu_profile) {
            return true;
        } else if (id == R.id.menu_logout) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}