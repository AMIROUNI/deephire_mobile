package com.example.deephire_android.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deephire_android.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigInActivity extends AppCompatActivity {

    private static final String TAG = "SigInActivity";
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignIn;
    private FirebaseFirestore db;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sigin);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        db = FirebaseFirestore.getInstance();

        // Initialize progress dialog
        progressDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_progress)
                .setCancelable(false)
                .create();

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        if (email != null) {
            etEmail.setText(email);
        }

        btnSignIn.setOnClickListener(this::sigIn);
    }

    public void sigIn(View view) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d(TAG, "sigIn: Attempting login with email: " + email);

        boolean isValid = true;

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            isValid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (!isValid) {
            showAlert("Attention!", "Please fill in all required fields correctly.");
            return;
        }

        progressDialog.show();

        // Check Firestore for matching user
        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressDialog.dismiss();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "sigIn: No user found for email: " + email);
                        Toast.makeText(this, "No user found with this email.", Toast.LENGTH_LONG).show();
                    } else {
                        var doc = queryDocumentSnapshots.getDocuments().get(0);
                        String dbPassword = doc.getString("password");
                        String role = doc.getString("role");

                        Log.d(TAG, "sigIn: User found, role: " + role);

                        if (dbPassword != null && dbPassword.equals(password)) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            redirectByRole(role, email);
                        } else {
                            Log.d(TAG, "sigIn: Incorrect password for email: " + email);
                            Toast.makeText(this, "Incorrect password.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "sigIn: Error fetching user: " + e.getMessage(), e);
                    showAlert("Error", "Login failed. Please try again.");
                });
    }

    public void redirectByRole(String role, String email) {
        Intent intent;
        Log.d(TAG, "redirectByRole: Redirecting user with role: " + role + ", email: " + email);
        switch (role) {
            case "Admin Company":
                intent = new Intent(this, CompliteProfileActivity.class);
                break;
            case "Admin":
                intent = new Intent(this, AdminActivity.class);
                break;
            default:
                intent = new Intent(this, ProfileActivity.class);
        }

        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}