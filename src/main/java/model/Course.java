package model;

import java.sql.Timestamp;

/**
 * Class đại diện cho Khóa học
 */
public class Course {
    private int courseId;
    private String courseName;
    private String courseCode;
    private String description;
    private int instructorId;
    private String instructorName; // Để hiển thị
    private int durationWeeks;
    private double price;
    private int maxStudents;
    private String status; // active, inactive, completed
    private Timestamp createdAt;

    // Constructor mặc định
    public Course() {
    }

    // Constructor đầy đủ
    public Course(int courseId, String courseName, String courseCode, String description,
                 int instructorId, String instructorName, int durationWeeks, double price,
                 int maxStudents, String status, Timestamp createdAt) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.description = description;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.durationWeeks = durationWeeks;
        this.price = price;
        this.maxStudents = maxStudents;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor không có ID (cho insert)
    public Course(String courseName, String courseCode, String description,
                 int instructorId, int durationWeeks, double price,
                 int maxStudents, String status) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.description = description;
        this.instructorId = instructorId;
        this.durationWeeks = durationWeeks;
        this.price = price;
        this.maxStudents = maxStudents;
        this.status = status;
    }

    // Getters và Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public int getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(int durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return courseName + " (" + courseCode + ")";
    }
}

