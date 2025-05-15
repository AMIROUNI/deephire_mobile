package com.example.deephire_android.Models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class Profile {
    private String email;
    private String specialty;
    private String bio;
    private String phoneNumber;

    public Profile() {
        this.email = "";
        this.bio = "";
        this.phoneNumber = "";
        this.specialty = "";
    }

    public Profile(String email, String bio, String phoneNumber, String specialty) {
        this.email = email;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
        this.specialty = specialty;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(gson.toJson(this), type);
    }

    public interface ProfileCallback {
        void onProfileFound(Profile profile);
        void onProfileNotFound();
        void onError(Exception e);
        void onProfileAdded(Profile profile);
    }

    public static void addProfile(Profile profile, @NonNull ProfileCallback callback) {
        if (profile == null || profile.getEmail() == null || profile.getEmail().trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("Profile or email cannot be null or empty"));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles")
                .document(profile.getEmail())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onError(new Exception("Profile already exists for this email"));
                    } else {
                        db.collection("profiles")
                                .document(profile.getEmail())
                                .set(profile.toMap())
                                .addOnSuccessListener(aVoid -> callback.onProfileAdded(profile))
                                .addOnFailureListener(e -> callback.onError(e));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
    }

    public static void getProfileByEmail(String email, @NonNull ProfileCallback callback) {
        if (email == null || email.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("Email cannot be null or empty"));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Profile profile = documentSnapshot.toObject(Profile.class);
                        callback.onProfileFound(profile);
                    } else {
                        callback.onProfileNotFound();
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
    }

    public static void updateProfile(Profile profile, @NonNull ProfileCallback callback) {
        if (profile == null || profile.getEmail() == null || profile.getEmail().trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("Profile or email cannot be null or empty"));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles")
                .document(profile.getEmail())
                .set(profile.toMap())
                .addOnSuccessListener(aVoid -> callback.onProfileFound(profile))
                .addOnFailureListener(e -> callback.onError(e));
    }
}