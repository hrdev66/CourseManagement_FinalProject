package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Class đại diện cho Bài nộp (Submission) của sinh viên
 */
public class Submission {
    private int submissionId;
    private int assignmentId;
    private int studentId;
    private String studentName; // Để hiển thị
    private String assignmentTitle; // Để hiển thị
    private String content;
    private String attachment;
    private Integer score;
    private String status; // submitted, graded, late
    private Date submittedDate;
    private Timestamp createdAt;

    // Constructor mặc định
    public Submission() {
    }

    // Constructor đầy đủ
    public Submission(int submissionId, int assignmentId, int studentId, String studentName,
                     String assignmentTitle, String content, String attachment, Integer score,
                     String status, Date submittedDate, Timestamp createdAt) {
        this.submissionId = submissionId;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.assignmentTitle = assignmentTitle;
        this.content = content;
        this.attachment = attachment;
        this.score = score;
        this.status = status;
        this.submittedDate = submittedDate;
        this.createdAt = createdAt;
    }

    // Constructor không có ID (cho insert)
    public Submission(int assignmentId, int studentId, String content, String attachment,
                     String status, Date submittedDate) {
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.content = content;
        this.attachment = attachment;
        this.status = status;
        this.submittedDate = submittedDate;
    }

    // Getters và Setters
    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
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

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return studentName + " - " + assignmentTitle;
    }
}

