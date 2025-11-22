package model;

import java.sql.Timestamp;

/**
 * Class đại diện cho Bài học
 */
public class Lesson {
    private int lessonId;
    private int courseId;
    private String lessonTitle;
    private int lessonOrder;
    private String content;
    private String videoUrl;
    private int durationMinutes;
    private Timestamp createdAt;

    // Constructor mặc định
    public Lesson() {
    }

    // Constructor đầy đủ
    public Lesson(int lessonId, int courseId, String lessonTitle, int lessonOrder,
                 String content, String videoUrl, int durationMinutes, Timestamp createdAt) {
        this.lessonId = lessonId;
        this.courseId = courseId;
        this.lessonTitle = lessonTitle;
        this.lessonOrder = lessonOrder;
        this.content = content;
        this.videoUrl = videoUrl;
        this.durationMinutes = durationMinutes;
        this.createdAt = createdAt;
    }

    // Constructor không có ID (cho insert)
    public Lesson(int courseId, String lessonTitle, int lessonOrder,
                 String content, String videoUrl, int durationMinutes) {
        this.courseId = courseId;
        this.lessonTitle = lessonTitle;
        this.lessonOrder = lessonOrder;
        this.content = content;
        this.videoUrl = videoUrl;
        this.durationMinutes = durationMinutes;
    }

    // Getters và Setters
    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public int getLessonOrder() {
        return lessonOrder;
    }

    public void setLessonOrder(int lessonOrder) {
        this.lessonOrder = lessonOrder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return lessonOrder + ". " + lessonTitle;
    }
}

