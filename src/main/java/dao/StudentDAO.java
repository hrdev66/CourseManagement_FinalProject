package dao;

import database.DatabaseConnection;
import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Student
 */
public class StudentDAO {
    private Connection connection;

    public StudentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Thêm sinh viên mới
     */
    public boolean insert(Student student) {
        String sql = "INSERT INTO students (full_name, email, phone, date_of_birth, address) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getFullName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.setDate(4, student.getDateOfBirth());
            pstmt.setString(5, student.getAddress());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    student.setStudentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm sinh viên: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật thông tin sinh viên
     */
    public boolean update(Student student) {
        String sql = "UPDATE students SET full_name = ?, email = ?, phone = ?, date_of_birth = ?, address = ? WHERE student_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getFullName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.setDate(4, student.getDateOfBirth());
            pstmt.setString(5, student.getAddress());
            pstmt.setInt(6, student.getStudentId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật sinh viên: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa sinh viên
     */
    public boolean delete(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa sinh viên: " + e.getMessage());
        }
        return false;
    }

    /**
     * Tìm sinh viên theo ID
     */
    public Student findById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm sinh viên: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy tất cả sinh viên
     */
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY full_name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách sinh viên: " + e.getMessage());
        }
        return students;
    }

    /**
     * Tìm kiếm sinh viên theo tên hoặc email
     */
    public List<Student> search(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE full_name LIKE ? OR email LIKE ? ORDER BY full_name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm sinh viên: " + e.getMessage());
        }
        return students;
    }

    /**
     * Trích xuất Student từ ResultSet
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setFullName(rs.getString("full_name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setAddress(rs.getString("address"));
        student.setEnrollmentDate(rs.getDate("enrollment_date"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        return student;
    }
}

