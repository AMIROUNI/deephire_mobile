package com.example.deephire_android.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deephire_android.Models.Company;
import com.example.deephire_android.Models.User;
import com.example.deephire_android.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class CompliteProfileActivity extends AppCompatActivity {

    private static final String TAG = "CompliteProfileActivity";
    private TextInputEditText etName, etPhone, etWebsite, etIndustry, etDescription, etLocation;
    private Button btnSubmit;
    private FirebaseFirestore db;
    private String email;
    private Gson gson = new Gson();
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complite_profile);


        Log.d(TAG, "onCreate: CompliteProfileActivity started");

        db = FirebaseFirestore.getInstance();
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etWebsite = findViewById(R.id.etWebsite);
        etIndustry = findViewById(R.id.etIndustry);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Initialize progress dialog
        progressDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_progress)
                .setCancelable(false)
                .create();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        Log.d(TAG, "Received email: " + email);

        if (email == null || email.trim().isEmpty()) {
            Log.e(TAG, "Email is null or empty");
            showErrorAlert("Email not provided. Please sign in again.", () -> finish());
            return;
        }

        // Disable submit button until user is verified
        btnSubmit.setEnabled(false);

        // Check isFirstLogin
        checkUserFirstLogin();
    }

    private void checkUserFirstLogin() {
        Log.d(TAG, "checkUserFirstLogin: Querying user for email: " + email);
        progressDialog.show();
        User.getUserByEmail(email, new User.UserCallback() {
            @Override
            public void onUserFound(User user) {
                progressDialog.dismiss();
                Log.d(TAG, "User found, isFirstLogin: " + user.isFirstLogin() + ", role: " + user.getRole());
                if (!user.isFirstLogin()) {
                    Log.d(TAG, "Redirecting to AdminCompanyActivity as isFirstLogin is false");
                    Intent intent1 = new Intent(CompliteProfileActivity.this, AdminCompanyActivity.class);
                    intent1.putExtra("email", email);
                    startActivity(intent1);
                    finish();
                } else {
                    Log.d(TAG, "Staying in CompliteProfileActivity as isFirstLogin is true");
                    btnSubmit.setEnabled(true);
                    btnSubmit.setOnClickListener(v -> saveCompany());
                }
            }

            @Override
            public void onUserNotFound() {
                progressDialog.dismiss();
                Log.e(TAG, "User not found for email: " + email);
                showErrorAlert("User not found. Please sign in again.", () -> checkUserFirstLogin());
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                Log.e(TAG, "Error fetching user: " + e.getMessage(), e);
                showErrorAlert("Unable to fetch user data. Please check your network and try again.", () -> checkUserFirstLogin());
            }
        });
    }

    public Map<String, Object> convertCompanyToMap(Company company) {
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(gson.toJson(company), type);
    }

    private void saveCompany() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String website = etWebsite.getText().toString().trim();
        String industry = etIndustry.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        boolean isValid = true;

        if (name.isEmpty()) {
            etName.setError("Company name is required");
            isValid = false;
        } else if (name.length() < 4) {
            etName.setError("Company name must be at least 4 characters");
            isValid = false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            isValid = false;
        } else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Enter a valid phone number");
            isValid = false;
        }

        if (website.isEmpty()) {
            etWebsite.setError("Website is required");
            isValid = false;
        } else if (!android.util.Patterns.WEB_URL.matcher(website).matches()) {
            etWebsite.setError("Enter a valid website URL");
            isValid = false;
        }

        if (location.isEmpty()) {
            etLocation.setError("Location is required");
            isValid = false;
        }

        if (industry.isEmpty()) {
            etIndustry.setError("Industry is required");
            isValid = false;
        }

        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            isValid = false;
        }

        if (!isValid) {
            showErrorAlert("Please fill in all required fields correctly.");
            return;
        }

        Company company = new Company(
                "",
                name,
                email,
                phone,
                website,
                industry,
                location,
                description,
                "",
                new ArrayList<>()
        );

        Map<String, Object> companyData = convertCompanyToMap(company);
        saveCompanyToFirestore(company, companyData);
    }

    private void saveCompanyToFirestore(Company company, Map<String, Object> companyData) {
        progressDialog.show();
        db.collection("companies")
                .add(companyData)
                .addOnSuccessListener(documentReference -> {
                    String companyId = documentReference.getId();
                    company.setId(companyId);

                    db.collection("companies").document(companyId)
                            .update("id", companyId)
                            .addOnSuccessListener(aVoid -> {
                                updateUserFirstLogin(companyId);
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                showErrorAlert("Failed to update company ID: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showErrorAlert("Failed to save company: " + e.getMessage());
                });
    }

    private void updateUserFirstLogin(String companyId) {
        User.getUserByEmail(email, new User.UserCallback() {
            @Override
            public void onUserFound(User user) {
                user.setFirstLogin(false);
                user.setCompanyId(companyId);

                User.updateUser(user, new User.UserCallback() {
                    @Override
                    public void onUserFound(User updatedUser) {
                        progressDialog.dismiss();
                        showSuccessAlert("Company added successfully!");
                    }

                    @Override
                    public void onUserNotFound() {
                        progressDialog.dismiss();
                        showErrorAlert("User not found during update.");
                    }

                    @Override
                    public void onError(Exception e) {
                        progressDialog.dismiss();
                        showErrorAlert("Failed to update user: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onUserNotFound() {
                progressDialog.dismiss();
                showErrorAlert("User not found.");
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                showErrorAlert("Failed to fetch user: " + e.getMessage());
            }
        });
    }

    private void showSuccessAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Success!");
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(this, AdminCompanyActivity.class);
            intent.putExtra("email", email);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        alertDialog.show();
    }

    private void showErrorAlert(String message) {
        showErrorAlert(message, null);
    }

    private void showErrorAlert(String message, Runnable onRetry) {
        Log.d(TAG, "Showing error alert: " + message);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        if (onRetry != null) {
            builder.setNegativeButton("Retry", (dialog, which) -> onRetry.run());
        }
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: CompliteProfileActivity destroyed");
    }
}