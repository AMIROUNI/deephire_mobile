package com.example.deephire_android.Models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class User {
    private String fullName;
    private String email;
    private String password;
    private String role;
    private boolean isBanned;
    private boolean isFirstLogin;
    private String companyId;

    // No-argument constructor for Firestore deserialization
    public User() {
        this.fullName = "";
        this.email = "";
        this.password = "";
        this.role = "";
        this.isBanned = false;
        this.isFirstLogin = true; // Default to true for new users
        this.companyId = "";
    }

    // Parameterized constructor
    public User(String fullName, String email, String password, String role, boolean isBanned) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isBanned = isBanned;
        this.isFirstLogin = true; // Default to true for new users
        this.companyId = "";
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }

    public boolean isFirstLogin() { return isFirstLogin; }
    public void setFirstLogin(boolean firstLogin) { isFirstLogin = firstLogin; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    // Callback interface for async operations
    public interface UserCallback {
        void onUserFound(User user);
        void onUserNotFound();
        void onError(Exception e);
    }

    // Convert User to Map for Firestore
    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(gson.toJson(this), type);
    }

    // Async method to get user by email
    public static void getUserByEmail(String email, @NonNull UserCallback callback) {
        if (email == null || email.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("Email cannot be null or empty"));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1) // Optimize for single result
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onUserNotFound();
                    } else {
                        User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                        // Ensure isFirstLogin is true if missing in Firestore
                        if (!queryDocumentSnapshots.getDocuments().get(0).contains("isFirstLogin")) {
                            user.setFirstLogin(true);
                        }
                        callback.onUserFound(user);
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
    }

    // Async method to update user
    public static void updateUser(User updatedUser, @NonNull UserCallback callback) {
        if (updatedUser == null || updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("User or email cannot be null or empty"));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = updatedUser.toMap();
        db.collection("users")
                .document(updatedUser.getEmail())
                .set(userData)
                .addOnSuccessListener(aVoid -> callback.onUserFound(updatedUser))
                .addOnFailureListener(e -> callback.onError(e));
    }
}