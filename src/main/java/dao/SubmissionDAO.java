package dao;

import database.DatabaseConnection;
import model.Submission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Submission
 */
public class SubmissionDAO {
    private Connection connection;

    public SubmissionDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Thêm bài nộp mới
     */
    public boolean insert(Submission submission) {
        String sql = "INSERT INTO submissions (assignment_id, student_id, content, " +
                     "attachment, status, submitted_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, submission.getAssignmentId());
            pstmt.setInt(2, submission.getStudentId());
            pstmt.setString(3, submission.getContent());
            pstmt.setString(4, submission.getAttachment());
            pstmt.setString(5, submission.getStatus());
            pstmt.setDate(6, submission.getSubmittedDate());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    submission.setSubmissionId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm bài nộp: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật bài nộp (chấm điểm)
     */
    public boolean update(Submission submission) {
        String sql = "UPDATE submissions SET score = ?, status = ? WHERE submission_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (submission.getScore() != null) {
                pstmt.setInt(1, submission.getScore());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, submission.getStatus());
            pstmt.setInt(3, submission.getSubmissionId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật bài nộp: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa bài nộp
     */
    public boolean delete(int submissionId) {
        String sql = "DELETE FROM submissions WHERE submission_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, submissionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa bài nộp: " + e.getMessage());
        }
        return false;
    }

    /**
     * Tìm bài nộp theo ID
     */
    public Submission findById(int submissionId) {
        String sql = "SELECT s.*, st.full_name as student_name, a.title as assignment_title " +
                     "FROM submissions s " +
                     "JOIN students st ON s.student_id = st.student_id " +
                     "JOIN assignments a ON s.assignment_id = a.assignment_id " +
                     "WHERE s.submission_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, submissionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractSubmissionFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm bài nộp: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy tất cả bài nộp
     */
    public List<Submission> findAll() {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.*, st.full_name as student_name, a.title as assignment_title " +
                     "FROM submissions s " +
                     "JOIN students st ON s.student_id = st.student_id " +
                     "JOIN assignments a ON s.assignment_id = a.assignment_id " +
                     "ORDER BY s.submitted_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                submissions.add(extractSubmissionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách bài nộp: " + e.getMessage());
        }
        return submissions;
    }

    /**
     * Lấy bài nộp theo sinh viên
     */
    public List<Submission> findByStudent(int studentId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.*, st.full_name as student_name, a.title as assignment_title " +
                     "FROM submissions s " +
                     "JOIN students st ON s.student_id = st.student_id " +
                     "JOIN assignments a ON s.assignment_id = a.assignment_id " +
                     "WHERE s.student_id = ? ORDER BY s.submitted_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                submissions.add(extractSubmissionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm bài nộp theo sinh viên: " + e.getMessage());
        }
        return submissions;
    }

    /**
     * Lấy bài nộp theo bài tập
     */
    public List<Submission> findByAssignment(int assignmentId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.*, st.full_name as student_name, a.title as assignment_title " +
                     "FROM submissions s " +
                     "JOIN students st ON s.student_id = st.student_id " +
                     "JOIN assignments a ON s.assignment_id = a.assignment_id " +
                     "WHERE s.assignment_id = ? ORDER BY s.submitted_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, assignmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                submissions.add(extractSubmissionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm bài nộp theo bài tập: " + e.getMessage());
        }
        return submissions;
    }

    /**
     * Trích xuất Submission từ ResultSet
     */
    private Submission extractSubmissionFromResultSet(ResultSet rs) throws SQLException {
        Submission submission = new Submission();
        submission.setSubmissionId(rs.getInt("submission_id"));
        submission.setAssignmentId(rs.getInt("assignment_id"));
        submission.setStudentId(rs.getInt("student_id"));
        submission.setStudentName(rs.getString("student_name"));
        submission.setAssignmentTitle(rs.getString("assignment_title"));
        submission.setContent(rs.getString("content"));
        submission.setAttachment(rs.getString("attachment"));
        
        int score = rs.getInt("score");
        if (!rs.wasNull()) {
            submission.setScore(score);
        }
        
        submission.setStatus(rs.getString("status"));
        submission.setSubmittedDate(rs.getDate("submitted_date"));
        submission.setCreatedAt(rs.getTimestamp("created_at"));
        return submission;
    }
}

