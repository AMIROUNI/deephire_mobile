package com.example.deephire_android.Models;

public class User {
    private String fullName;
    private String email;
    private String password;
    private String role;


    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    private boolean isBanned ;

    private boolean isFirstLogin;

    public User(String fullName, String email, String password, String role, boolean isBanned ) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isBanned=isBanned;
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
}
