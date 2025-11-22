package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Class đại diện cho Đăng ký khóa học
 */
public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int courseId;
    private String studentName; // Để hiển thị
    private String courseName;  // Để hiển thị
    private Date enrollmentDate;
    private String completionStatus; // enrolled, in_progress, completed, dropped
    private Double grade;
    private String paymentStatus; // pending, paid, refunded
    private Timestamp createdAt;

    // Constructor mặc định
    public Enrollment() {
    }

    // Constructor đầy đủ
    public Enrollment(int enrollmentId, int studentId, int courseId, String studentName,
                     String courseName, Date enrollmentDate, String completionStatus,
                     Double grade, String paymentStatus, Timestamp createdAt) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.studentName = studentName;
        this.courseName = courseName;
        this.enrollmentDate = enrollmentDate;
        this.completionStatus = completionStatus;
        this.grade = grade;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    // Constructor không có ID (cho insert)
    public Enrollment(int studentId, int courseId, Date enrollmentDate,
                     String completionStatus, Double grade, String paymentStatus) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.completionStatus = completionStatus;
        this.grade = grade;
        this.paymentStatus = paymentStatus;
    }

    // Getters và Setters
    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return studentName + " - " + courseName;
    }
}

