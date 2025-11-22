package model;

import java.sql.Timestamp;

/**
 * Class đại diện cho Giảng viên
 */
public class Instructor {
    private int instructorId;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private String bio;
    private Timestamp createdAt;

    // Constructor mặc định
    public Instructor() {
    }

    // Constructor đầy đủ
    public Instructor(int instructorId, String fullName, String email, String phone, 
                     String specialization, String bio, Timestamp createdAt) {
        this.instructorId = instructorId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.bio = bio;
        this.createdAt = createdAt;
    }

    // Constructor không có ID (cho insert)
    public Instructor(String fullName, String email, String phone, 
                     String specialization, String bio) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.bio = bio;
    }

    // Getters và Setters
    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return fullName + " (" + specialization + ")";
    }
}

