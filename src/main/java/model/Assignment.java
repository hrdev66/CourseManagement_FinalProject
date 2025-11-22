package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Class đại diện cho Bài tập (Assignment)
 */
public class Assignment {
    private int assignmentId;
    private int courseId;
    private String courseName; // Để hiển thị
    private String title;
    private String description;
    private Date dueDate;
    private int maxScore;
    private String assignmentType; // homework, quiz, project
    private String status; // published, draft
    private Timestamp createdAt;

    // Constructor mặc định
    public Assignment() {
    }

    // Constructor đầy đủ
    public Assignment(int assignmentId, int courseId, String courseName, String title,
                     String description, Date dueDate, int maxScore, String assignmentType,
                     String status, Timestamp createdAt) {
        this.assignmentId = assignmentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
        this.assignmentType = assignmentType;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor không có ID (cho insert)
    public Assignment(int courseId, String title, String description, Date dueDate,
                     int maxScore, String assignmentType, String status) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
        this.assignmentType = assignmentType;
        this.status = status;
    }

    // Getters và Setters
    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
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
        return title + " (" + courseName + ")";
    }
}

