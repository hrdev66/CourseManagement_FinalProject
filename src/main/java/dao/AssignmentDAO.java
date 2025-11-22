package dao;

import database.DatabaseConnection;
import model.Assignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Assignment
 */
public class AssignmentDAO {
    private Connection connection;

    public AssignmentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Thêm bài tập mới
     */
    public boolean insert(Assignment assignment) {
        String sql = "INSERT INTO assignments (course_id, title, description, due_date, " +
                     "max_score, assignment_type, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, assignment.getCourseId());
            pstmt.setString(2, assignment.getTitle());
            pstmt.setString(3, assignment.getDescription());
            pstmt.setDate(4, assignment.getDueDate());
            pstmt.setInt(5, assignment.getMaxScore());
            pstmt.setString(6, assignment.getAssignmentType());
            pstmt.setString(7, assignment.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    assignment.setAssignmentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm bài tập: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật bài tập
     */
    public boolean update(Assignment assignment) {
        String sql = "UPDATE assignments SET course_id = ?, title = ?, description = ?, " +
                     "due_date = ?, max_score = ?, assignment_type = ?, status = ? " +
                     "WHERE assignment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, assignment.getCourseId());
            pstmt.setString(2, assignment.getTitle());
            pstmt.setString(3, assignment.getDescription());
            pstmt.setDate(4, assignment.getDueDate());
            pstmt.setInt(5, assignment.getMaxScore());
            pstmt.setString(6, assignment.getAssignmentType());
            pstmt.setString(7, assignment.getStatus());
            pstmt.setInt(8, assignment.getAssignmentId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật bài tập: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa bài tập
     */
    public boolean delete(int assignmentId) {
        String sql = "DELETE FROM assignments WHERE assignment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, assignmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa bài tập: " + e.getMessage());
        }
        return false;
    }

    /**
     * Tìm bài tập theo ID
     */
    public Assignment findById(int assignmentId) {
        String sql = "SELECT a.*, c.course_name FROM assignments a " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.assignment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, assignmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractAssignmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm bài tập: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy tất cả bài tập
     */
    public List<Assignment> findAll() {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name FROM assignments a " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "ORDER BY a.due_date DESC, a.created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                assignments.add(extractAssignmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách bài tập: " + e.getMessage());
        }
        return assignments;
    }

    /**
     * Lấy bài tập theo khóa học
     */
    public List<Assignment> findByCourse(int courseId) {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name FROM assignments a " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.course_id = ? ORDER BY a.due_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                assignments.add(extractAssignmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm bài tập theo khóa học: " + e.getMessage());
        }
        return assignments;
    }

    /**
     * Trích xuất Assignment từ ResultSet
     */
    private Assignment extractAssignmentFromResultSet(ResultSet rs) throws SQLException {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(rs.getInt("assignment_id"));
        assignment.setCourseId(rs.getInt("course_id"));
        assignment.setCourseName(rs.getString("course_name"));
        assignment.setTitle(rs.getString("title"));
        assignment.setDescription(rs.getString("description"));
        assignment.setDueDate(rs.getDate("due_date"));
        assignment.setMaxScore(rs.getInt("max_score"));
        assignment.setAssignmentType(rs.getString("assignment_type"));
        assignment.setStatus(rs.getString("status"));
        assignment.setCreatedAt(rs.getTimestamp("created_at"));
        return assignment;
    }
}

