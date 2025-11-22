package dao;

import database.DatabaseConnection;
import model.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Enrollment
 */
public class EnrollmentDAO {
    private Connection connection;

    public EnrollmentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Thêm đăng ký mới
     */
    public boolean insert(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date, " +
                     "completion_status, grade, payment_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setDate(3, enrollment.getEnrollmentDate());
            pstmt.setString(4, enrollment.getCompletionStatus());
            if (enrollment.getGrade() != null) {
                pstmt.setDouble(5, enrollment.getGrade());
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }
            pstmt.setString(6, enrollment.getPaymentStatus());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    enrollment.setEnrollmentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm đăng ký: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật thông tin đăng ký
     */
    public boolean update(Enrollment enrollment) {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ?, enrollment_date = ?, " +
                     "completion_status = ?, grade = ?, payment_status = ? WHERE enrollment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setDate(3, enrollment.getEnrollmentDate());
            pstmt.setString(4, enrollment.getCompletionStatus());
            if (enrollment.getGrade() != null) {
                pstmt.setDouble(5, enrollment.getGrade());
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }
            pstmt.setString(6, enrollment.getPaymentStatus());
            pstmt.setInt(7, enrollment.getEnrollmentId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật đăng ký: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa đăng ký
     */
    public boolean delete(int enrollmentId) {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa đăng ký: " + e.getMessage());
        }
        return false;
    }

    /**
     * Tìm đăng ký theo ID
     */
    public Enrollment findById(int enrollmentId) {
        String sql = "SELECT e.*, s.full_name as student_name, c.course_name " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE e.enrollment_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractEnrollmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm đăng ký: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy tất cả đăng ký
     */
    public List<Enrollment> findAll() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.*, s.full_name as student_name, c.course_name " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "ORDER BY e.enrollment_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách đăng ký: " + e.getMessage());
        }
        return enrollments;
    }

    /**
     * Lấy đăng ký theo sinh viên
     */
    public List<Enrollment> findByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.*, s.full_name as student_name, c.course_name " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE e.student_id = ? ORDER BY e.enrollment_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm đăng ký theo sinh viên: " + e.getMessage());
        }
        return enrollments;
    }

    /**
     * Lấy đăng ký theo khóa học
     */
    public List<Enrollment> findByCourse(int courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.*, s.full_name as student_name, c.course_name " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE e.course_id = ? ORDER BY e.enrollment_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm đăng ký theo khóa học: " + e.getMessage());
        }
        return enrollments;
    }

    /**
     * Kiểm tra sinh viên đã đăng ký khóa học chưa
     */
    public boolean isEnrolled(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) as count FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra đăng ký: " + e.getMessage());
        }
        return false;
    }

    /**
     * Trích xuất Enrollment từ ResultSet
     */
    private Enrollment extractEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setCourseId(rs.getInt("course_id"));
        enrollment.setStudentName(rs.getString("student_name"));
        enrollment.setCourseName(rs.getString("course_name"));
        enrollment.setEnrollmentDate(rs.getDate("enrollment_date"));
        enrollment.setCompletionStatus(rs.getString("completion_status"));
        
        double grade = rs.getDouble("grade");
        if (!rs.wasNull()) {
            enrollment.setGrade(grade);
        }
        
        enrollment.setPaymentStatus(rs.getString("payment_status"));
        enrollment.setCreatedAt(rs.getTimestamp("created_at"));
        return enrollment;
    }
}

