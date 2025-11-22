package com.coursemanagement.dto;

public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private String fullName;
    private int userId;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, String role, String fullName, int userId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.userId = userId;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

