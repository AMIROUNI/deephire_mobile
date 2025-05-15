package com.example.deephire_android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deephire_android.Models.Profile;
import com.example.deephire_android.R;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileActivity extends AppCompatActivity {

    private String email;
    private EditText etBio, etPhoneNumber, etSpecialty;
    private Button btnSaveProfile;
    private TextInputLayout bioContainer, phoneContainer, specialtyContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        if(email == null || email.isEmpty()){
            Toast.makeText(this, "Error: No email provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        checkExistingProfile();
        setupSaveButton();
    }

    private void initializeViews() {
        etBio = findViewById(R.id.etBio);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etSpecialty = findViewById(R.id.etSpecialty);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        bioContainer = findViewById(R.id.bioContainer);
        phoneContainer = findViewById(R.id.phoneContainer);
        specialtyContainer = findViewById(R.id.specialtyContainer);
    }

    private void setupSaveButton() {
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void checkExistingProfile() {
        Profile.getProfileByEmail(email, new Profile.ProfileCallback() {
            @Override
            public void onProfileFound(Profile profile) {
                navigateToUserActivity();
            }

            @Override
            public void onProfileNotFound() {
               
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onProfileAdded(Profile profile) {
            }
        });
    }

    private void navigateToUserActivity() {
        Intent intent = new Intent(ProfileActivity.this, UserActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void saveProfile() {
        String bio = etBio.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String specialty = etSpecialty.getText().toString().trim();

        if (!validateInputs(bio, phoneNumber, specialty)) return;

        Profile profile = new Profile(email, bio, phoneNumber, specialty);
        saveProfileToFirestore(profile);
    }

    private boolean validateInputs(String bio, String phoneNumber, String specialty) {
        boolean isValid = true;

        if (bio.isEmpty()) {
            bioContainer.setError("Bio cannot be empty");
            isValid = false;
        } else {
            bioContainer.setError(null);
        }

        if (phoneNumber.isEmpty()) {
            phoneContainer.setError("Phone number cannot be empty");
            isValid = false;
        } else {
            phoneContainer.setError(null);
        }

        if (specialty.isEmpty()) {
            specialtyContainer.setError("Specialty cannot be empty");
            isValid = false;
        } else {
            specialtyContainer.setError(null);
        }

        return isValid;
    }

    private void saveProfileToFirestore(Profile profile) {
        Profile.updateProfile(profile, new Profile.ProfileCallback() {
            @Override
            public void onProfileFound(Profile profile) {
                handleSaveSuccess();
            }

            @Override
            public void onProfileNotFound() {
                createNewProfile(profile);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProfileAdded(Profile profile) {
                // Not used here
            }
        });
    }

    private void createNewProfile(Profile profile) {
        Profile.addProfile(profile, new Profile.ProfileCallback() {
            @Override
            public void onProfileAdded(Profile profile) {
                handleSaveSuccess();
            }

            @Override
            public void onProfileFound(Profile profile) {}
            @Override
            public void onProfileNotFound() {}
            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileActivity.this, "Create failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSaveSuccess() {
        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
        navigateToUserActivity();
    }
}