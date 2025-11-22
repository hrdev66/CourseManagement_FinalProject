package dao;

import database.DatabaseConnection;
import model.Instructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Instructor
 */
public class InstructorDAO {
    private Connection connection;

    public InstructorDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Thêm giảng viên mới
     */
    public boolean insert(Instructor instructor) {
        String sql = "INSERT INTO instructors (full_name, email, phone, specialization, bio) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, instructor.getFullName());
            pstmt.setString(2, instructor.getEmail());
            pstmt.setString(3, instructor.getPhone());
            pstmt.setString(4, instructor.getSpecialization());
            pstmt.setString(5, instructor.getBio());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    instructor.setInstructorId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm giảng viên: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật thông tin giảng viên
     */
    public boolean update(Instructor instructor) {
        String sql = "UPDATE instructors SET full_name = ?, email = ?, phone = ?, specialization = ?, bio = ? WHERE instructor_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, instructor.getFullName());
            pstmt.setString(2, instructor.getEmail());
            pstmt.setString(3, instructor.getPhone());
            pstmt.setString(4, instructor.getSpecialization());
            pstmt.setString(5, instructor.getBio());
            pstmt.setInt(6, instructor.getInstructorId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật giảng viên: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa giảng viên
     */
    public boolean delete(int instructorId) {
        String sql = "DELETE FROM instructors WHERE instructor_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, instructorId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa giảng viên: " + e.getMessage());
        }
        return false;
    }

    /**
     * Tìm giảng viên theo ID
     */
    public Instructor findById(int instructorId) {
        String sql = "SELECT * FROM instructors WHERE instructor_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractInstructorFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm giảng viên: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy tất cả giảng viên
     */
    public List<Instructor> findAll() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT * FROM instructors ORDER BY full_name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                instructors.add(extractInstructorFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách giảng viên: " + e.getMessage());
        }
        return instructors;
    }

    /**
     * Tìm kiếm giảng viên theo tên hoặc email
     */
    public List<Instructor> search(String keyword) {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT * FROM instructors WHERE full_name LIKE ? OR email LIKE ? ORDER BY full_name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                instructors.add(extractInstructorFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm giảng viên: " + e.getMessage());
        }
        return instructors;
    }

    /**
     * Trích xuất Instructor từ ResultSet
     */
    private Instructor extractInstructorFromResultSet(ResultSet rs) throws SQLException {
        Instructor instructor = new Instructor();
        instructor.setInstructorId(rs.getInt("instructor_id"));
        instructor.setFullName(rs.getString("full_name"));
        instructor.setEmail(rs.getString("email"));
        instructor.setPhone(rs.getString("phone"));
        instructor.setSpecialization(rs.getString("specialization"));
        instructor.setBio(rs.getString("bio"));
        instructor.setCreatedAt(rs.getTimestamp("created_at"));
        return instructor;
    }
}

