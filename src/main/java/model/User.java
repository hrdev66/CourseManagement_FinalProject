package model;

import java.sql.Timestamp;

/**
 * Class đại diện cho User/Tài khoản
 */
public class User {
    private int userId;
    private String username;
    private String password; // Hashed password
    private String email;
    private String role; // admin, instructor, student
    private int referenceId; // ID của instructor hoặc student (nếu không phải admin)
    private String fullName; // Để hiển thị
    private Timestamp createdAt;
    private Timestamp lastLogin;

    // Constructor mặc định
    public User() {
    }

    // Constructor đầy đủ
    public User(int userId, String username, String password, String email, String role,
                int referenceId, String fullName, Timestamp createdAt, Timestamp lastLogin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.referenceId = referenceId;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Constructor không có ID (cho insert)
    public User(String username, String password, String email, String role, int referenceId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.referenceId = referenceId;
    }

    // Constructor cho đăng ký
    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.referenceId = 0;
    }

    // Getters và Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }

    // Kiểm tra quyền
    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean isInstructor() {
        return "instructor".equals(role);
    }

    public boolean isStudent() {
        return "student".equals(role);
    }
}

