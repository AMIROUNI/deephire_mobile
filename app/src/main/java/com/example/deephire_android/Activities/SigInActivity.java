package com.example.deephire_android.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deephire_android.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigInActivity extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    Button btnSignIn;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sigin);

        this.etEmail = findViewById(R.id.etEmail);
        this.etPassword = findViewById(R.id.etPassword);
        this.btnSignIn = findViewById(R.id.btnSignIn);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        if (email != null) {
            etEmail.setText(email);
        }

        btnSignIn.setOnClickListener(v -> sigIn(v));
    }

    public void sigIn(View view) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() && password.isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Attention!");
            alertDialog.setMessage("All inputs are required.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        } else {
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

            if (!isValid) return;

            // Check Firestore for matching user
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(this, "No user found with this email.", Toast.LENGTH_LONG).show();
                        } else {
                            boolean match = false;

                            for (var doc : queryDocumentSnapshots) {
                                String dbPassword = doc.getString("password");
                                String role = doc.getString("role");

                                if (dbPassword != null && dbPassword.equals(password)) {
                                    match = true;

                                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                                    // Redirect user based on role
                                    redirectByRole(role,email);
                                }
                            }

                            if (!match) {
                                Toast.makeText(this, "Incorrect password.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    public void redirectByRole(String role,String email) {
        Intent intent;

        // Redirect user based on role
        switch (role) {
            case "Admin Company":
                intent = new Intent(this, AdminCompanyActivity.class);
                break;
            case "Admin":
                intent = new Intent(this, AdminActivity.class);
                break;
            default:
                intent = new Intent(this, UserActivity.class);
        }

        intent.putExtra("email",email);
        startActivity(intent);
        finish(); // Close the current activity (login screen)
    }
}
