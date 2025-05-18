package com.example.deephire_android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.deephire_android.Models.Profile;
import com.example.deephire_android.Models.User;
import com.example.deephire_android.R;


public class ProfileDetailActivity extends AppCompatActivity {

    private String email;
    private TextView fullNameText;
    private TextView roleText;
    private TextView emailText;
    private TextView phoneText;
    private TextView specialtyText;
    private TextView companyText;
    private TextView bioText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_details);


        fullNameText = findViewById(R.id.fullNameText);
        roleText = findViewById(R.id.roleText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        specialtyText = findViewById(R.id.specialtyText);
        companyText = findViewById(R.id.companyText);
        bioText = findViewById(R.id.bioText);

        Intent intent = getIntent();
        this.email = intent.getStringExtra("email");

        if(this.email == null){
            Toast.makeText(this,"Error: You are not logged in",Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, SigInActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        loadUserData();


    }

    private void loadUserData() {
        User.getUserByEmail(email, new User.UserCallback() {
            @Override
            public void onUserFound(User user) {
                // Update UI with user data
                fullNameText.setText(user.getFullName());
                roleText.setText(user.getRole());
                emailText.setText(user.getEmail());

                // Now load profile data
                loadProfileData(user);
            }

            @Override
            public void onUserNotFound() {
                Toast.makeText(ProfileDetailActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileDetailActivity.this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadProfileData(User user) {
        Profile.getProfileByEmail(email, new Profile.ProfileCallback() {
            @Override
            public void onProfileFound(Profile profile) {
                phoneText.setText(profile.getPhoneNumber());
                specialtyText.setText(profile.getSpecialty());
                bioText.setText(profile.getBio());


                if (user.getCompanyId() != null && !user.getCompanyId().isEmpty()) {
                    companyText.setText(user.getCompanyId());
                } else {
                    companyText.setText("Not specified");
                }
            }

            @Override
            public void onProfileNotFound() {

                phoneText.setText("Not specified");
                specialtyText.setText("Not specified");
                bioText.setText("No bio available");
                companyText.setText(user.getCompanyId() != null && !user.getCompanyId().isEmpty() ?
                        user.getCompanyId() : "Not specified");
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileDetailActivity.this, "Error loading profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProfileAdded(Profile profile) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}