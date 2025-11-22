package model;

import java.sql.Timestamp;

/**
 * Class đại diện cho Thông báo (Announcement)
 */
public class Announcement {
    private int announcementId;
    private int courseId;
    private String courseName; // Để hiển thị
    private int instructorId;
    private String instructorName; // Để hiển thị
    private String title;
    private String content;
    private String priority; // normal, important, urgent
    private Timestamp createdAt;

    // Constructor mặc định
    public Announcement() {
    }

    // Constructor đầy đủ
    public Announcement(int announcementId, int courseId, String courseName, int instructorId,
                       String instructorName, String title, String content, String priority,
                       Timestamp createdAt) {
        this.announcementId = announcementId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    // Constructor không có ID (cho insert)
    public Announcement(int courseId, int instructorId, String title, String content, String priority) {
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.title = title;
        this.content = content;
        this.priority = priority;
    }

    // Getters và Setters
    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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

