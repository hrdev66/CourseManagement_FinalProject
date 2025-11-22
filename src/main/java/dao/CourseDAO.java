package dao;

import database.DatabaseConnection;
import model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Course
 */
public class CourseDAO {
    private Connection connection;

    public CourseDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Thêm khóa học mới
     */
    public boolean insert(Course course) {
        String sql = "INSERT INTO courses (course_name, course_code, description, instructor_id, " +
                     "duration_weeks, price, max_students, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getCourseCode());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getInstructorId());
            pstmt.setInt(5, course.getDurationWeeks());
            pstmt.setDouble(6, course.getPrice());
            pstmt.setInt(7, course.getMaxStudents());
            pstmt.setString(8, course.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    course.setCourseId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm khóa học: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật thông tin khóa học
     */
    public boolean update(Course course) {
        String sql = "UPDATE courses SET course_name = ?, course_code = ?, description = ?, " +
                     "instructor_id = ?, duration_weeks = ?, price = ?, max_students = ?, status = ? " +
                     "WHERE course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getCourseCode());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getInstructorId());
            pstmt.setInt(5, course.getDurationWeeks());
            pstmt.setDouble(6, course.getPrice());
            pstmt.setInt(7, course.getMaxStudents());
            pstmt.setString(8, course.getStatus());
            pstmt.setInt(9, course.getCourseId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật khóa học: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa khóa học
     */
    public boolean delete(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa khóa học: " + e.getMessage());
        }
        return false;
    }

    /**
     * Tìm khóa học theo ID
     */
    public Course findById(int courseId) {
        String sql = "SELECT c.*, i.full_name as instructor_name FROM courses c " +
                     "LEFT JOIN instructors i ON c.instructor_id = i.instructor_id " +
                     "WHERE c.course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractCourseFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm khóa học: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy tất cả khóa học
     */
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, i.full_name as instructor_name FROM courses c " +
                     "LEFT JOIN instructors i ON c.instructor_id = i.instructor_id " +
                     "ORDER BY c.course_name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách khóa học: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Tìm khóa học theo giảng viên
     */
    public List<Course> findByInstructor(int instructorId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, i.full_name as instructor_name FROM courses c " +
                     "LEFT JOIN instructors i ON c.instructor_id = i.instructor_id " +
                     "WHERE c.instructor_id = ? ORDER BY c.course_name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm khóa học theo giảng viên: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Tìm kiếm khóa học theo tên hoặc mã
     */
    public List<Course> search(String keyword) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, i.full_name as instructor_name FROM courses c " +
                     "LEFT JOIN instructors i ON c.instructor_id = i.instructor_id " +
                     "WHERE c.course_name LIKE ? OR c.course_code LIKE ? ORDER BY c.course_name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm khóa học: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Lấy khóa học theo trạng thái
     */
    public List<Course> findByStatus(String status) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, i.full_name as instructor_name FROM courses c " +
                     "LEFT JOIN instructors i ON c.instructor_id = i.instructor_id " +
                     "WHERE c.status = ? ORDER BY c.course_name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm khóa học theo trạng thái: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Trích xuất Course từ ResultSet
     */
    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseName(rs.getString("course_name"));
        course.setCourseCode(rs.getString("course_code"));
        course.setDescription(rs.getString("description"));
        course.setInstructorId(rs.getInt("instructor_id"));
        course.setInstructorName(rs.getString("instructor_name"));
        course.setDurationWeeks(rs.getInt("duration_weeks"));
        course.setPrice(rs.getDouble("price"));
        course.setMaxStudents(rs.getInt("max_students"));
        course.setStatus(rs.getString("status"));
        course.setCreatedAt(rs.getTimestamp("created_at"));
        return course;
    }
}

