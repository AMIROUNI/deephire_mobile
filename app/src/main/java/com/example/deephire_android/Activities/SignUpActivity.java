package com.example.deephire_android.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.deephire_android.Models.User;
import com.example.deephire_android.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etPassword;
    RadioGroup roleRadioGroup;
    Button btnSignUp;

    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        etFullName = (EditText) findViewById(R.id.etFullName);
        etEmail =(EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        roleRadioGroup = (RadioGroup) findViewById(R.id.roleRadioGroup);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);


        db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(v -> signUp(v));
    }


    public Map<String, Object> convertUserToMap(User user) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(gson.toJson(user), type);
    }



    public void signUp(View view) {
        String fullName = etFullName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        int selectedId = roleRadioGroup.getCheckedRadioButtonId();

        if (fullName.isEmpty() && email.isEmpty() && password.isEmpty() && selectedId == -1) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Attention!");
            alertDialog.setMessage("All inputs are required.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        } else {
            boolean isValid = true;

            // Validation
            if (fullName.isEmpty()) {
                etFullName.setError("Full name is required");
                isValid = false;
            } else if (fullName.length() < 8) {
                etFullName.setError("Full name must be at least 8 characters");
                isValid = false;
            }

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

            if (selectedId == -1) {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            if (!isValid) return;

            String selectedRole = ((android.widget.RadioButton) findViewById(selectedId)).getText().toString();
            User user = new User(fullName, email, password, selectedRole, false);
            Map<String, Object> userData = convertUserToMap(user);

            // Check if email is already used
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Email is already in use. Please use a different one.");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
                            alertDialog.show();
                        } else {
                            // Email is unique – save user
                            db.collection("users")
                                    .document(user.getEmail())
                                    .set(userData)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(this, SigInActivity.class);
                                        intent.putExtra("email",user.getEmail());
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error checking email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }


}