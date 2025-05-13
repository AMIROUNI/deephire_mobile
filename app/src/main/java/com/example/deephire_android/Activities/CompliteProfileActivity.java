package com.example.deephire_android.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deephire_android.Models.Company;
import com.example.deephire_android.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class CompliteProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etWebsite, etIndustry, etDescription,efLocation;
    private Button btnSubmit;
    private FirebaseFirestore db;

    private String email;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        db = FirebaseFirestore.getInstance();
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etWebsite = findViewById(R.id.etWebsite);
        etIndustry = findViewById(R.id.etIndustry);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        efLocation=findViewById(R.id.etLocation);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        if (email == null) {
            Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSubmit.setOnClickListener(v -> saveCompany());
    }

    public Map<String, Object> convertCompanyToMap(Company company) {
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(gson.toJson(company), type);
    }

    private void saveCompany() {
        // Get all values
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String website = etWebsite.getText().toString().trim();
        String industry = etIndustry.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = efLocation.getText().toString().trim();

        // Validate all fields using your preferred style
        if (name.isEmpty() && phone.isEmpty() && website.isEmpty() && industry.isEmpty()
                && description.isEmpty() && location.isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Attention!");
            alertDialog.setMessage("All inputs are required.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
            alertDialog.show();
            return;
        }


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
            etWebsite.setError("location is required!");
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

        if (!isValid) return;


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

        // Convert to Map using Gson
        Map<String, Object> companyData = convertCompanyToMap(company);

        // Save to Firestore
        saveCompanyToFirestore(company, companyData);
    }

    private void saveCompanyToFirestore(Company company, Map<String, Object> companyData) {
        // Add document to Firestore
        db.collection("companies")
                .add(companyData)
                .addOnSuccessListener(documentReference -> {
                    // Update the company with the generated ID
                    String companyId = documentReference.getId();
                    company.setId(companyId);


                    db.collection("companies").document(companyId)
                            .update("id", companyId)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "worked: " + "add company succefully", Toast.LENGTH_LONG).show();

                            })
                            .addOnFailureListener(e -> {
                                showErrorAlert("Error updating company ID");
                            });
                })
                .addOnFailureListener(e -> {
                    showErrorAlert("Failed to save company: " + e.getMessage());
                });
    }


    private void showSuccessAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Success!");
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> {
            dialog.dismiss();
            navigateToMainActivity();
        });
        alertDialog.show();
    }

    private void showErrorAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}