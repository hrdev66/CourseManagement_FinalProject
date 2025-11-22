package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Class đại diện cho Sinh viên
 */
public class Student {
    private int studentId;
    private String fullName;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private String address;
    private Date enrollmentDate;
    private Timestamp createdAt;

    // Constructor mặc định
    public Student() {
    }

    // Constructor đầy đủ
    public Student(int studentId, String fullName, String email, String phone,
                  Date dateOfBirth, String address, Date enrollmentDate, Timestamp createdAt) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.enrollmentDate = enrollmentDate;
        this.createdAt = createdAt;
    }

    // Constructor không có ID (cho insert)
    public Student(String fullName, String email, String phone,
                  Date dateOfBirth, String address) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    // Getters và Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return fullName + " (" + email + ")";
    }
}

