package model;

import java.sql.Timestamp;

/**
 * Class đại diện cho Đánh giá khóa học
 */
public class Review {
    private int reviewId;
    private int courseId;
    private int studentId;
    private String studentName; // Để hiển thị
    private String courseName;  // Để hiển thị
    private int rating; // 1-5 sao
    private String comment;
    private Timestamp reviewDate;

    // Constructor mặc định
    public Review() {
    }

    // Constructor đầy đủ
    public Review(int reviewId, int courseId, int studentId, String studentName,
                 String courseName, int rating, String comment, Timestamp reviewDate) {
        this.reviewId = reviewId;
        this.courseId = courseId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseName = courseName;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    // Constructor không có ID (cho insert)
    public Review(int courseId, int studentId, int rating, String comment) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters và Setters
    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Timestamp reviewDate) {
        this.reviewDate = reviewDate;
    }

    @Override
    public String toString() {
        return studentName + " - " + rating + " sao";
    }
}

