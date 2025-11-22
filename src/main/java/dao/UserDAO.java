package dao;

import database.DatabaseConnection;
import model.User;
import util.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho User (Authentication)
 */
public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Đăng nhập - Kiểm tra username và password
     */
    public User login(String username, String password) {
        String sql = "SELECT u.*, " +
                     "CASE " +
                     "  WHEN u.role = 'instructor' THEN i.full_name " +
                     "  WHEN u.role = 'student' THEN s.full_name " +
                     "  ELSE 'Administrator' " +
                     "END as full_name " +
                     "FROM users u " +
                     "LEFT JOIN instructors i ON u.role = 'instructor' AND u.reference_id = i.instructor_id " +
                     "LEFT JOIN students s ON u.role = 'student' AND u.reference_id = s.student_id " +
                     "WHERE u.username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                
                // Kiểm tra password
                if (PasswordHasher.verifyPassword(password, hashedPassword)) {
                    User user = extractUserFromResultSet(rs);
                    
                    // Cập nhật last_login
                    updateLastLogin(user.getUserId());
                    
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
        }
        return null;
    }

    /**
     * Đăng ký tài khoản mới (mặc định là student)
     */
    public boolean register(User user) {
        // Kiểm tra username đã tồn tại chưa
        if (usernameExists(user.getUsername())) {
            return false;
        }
        
        // Kiểm tra email đã tồn tại chưa
        if (emailExists(user.getEmail())) {
            return false;
        }
        
        // Đảm bảo role là student (mặc định cho đăng ký mới)
        if (!"student".equals(user.getRole()) && !"admin".equals(user.getRole()) && !"instructor".equals(user.getRole())) {
            user.setRole("student");
        }
        
        // Hash password
        String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
        
        String sql = "INSERT INTO users (username, password, email, role, reference_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            pstmt.setInt(5, user.getReferenceId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký: " + e.getMessage());
        }
        return false;
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra username: " + e.getMessage());
        }
        return false;
    }

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật last login
     */
    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật last login: " + e.getMessage());
        }
    }

    /**
     * Tìm user theo ID
     */
    public User findById(int userId) {
        String sql = "SELECT u.*, " +
                     "CASE " +
                     "  WHEN u.role = 'instructor' AND i.instructor_id IS NOT NULL THEN i.full_name " +
                     "  WHEN u.role = 'student' AND s.student_id IS NOT NULL THEN s.full_name " +
                     "  WHEN u.role = 'admin' THEN 'Administrator' " +
                     "  ELSE u.username " +
                     "END as full_name " +
                     "FROM users u " +
                     "LEFT JOIN instructors i ON u.role = 'instructor' AND u.reference_id = i.instructor_id " +
                     "LEFT JOIN students s ON u.role = 'student' AND u.reference_id = s.student_id " +
                     "WHERE u.user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy tất cả users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, " +
                     "CASE " +
                     "  WHEN u.role = 'instructor' AND i.instructor_id IS NOT NULL THEN i.full_name " +
                     "  WHEN u.role = 'student' AND s.student_id IS NOT NULL THEN s.full_name " +
                     "  WHEN u.role = 'admin' THEN 'Administrator' " +
                     "  ELSE u.username " +
                     "END as full_name " +
                     "FROM users u " +
                     "LEFT JOIN instructors i ON u.role = 'instructor' AND u.reference_id = i.instructor_id " +
                     "LEFT JOIN students s ON u.role = 'student' AND u.reference_id = s.student_id " +
                     "ORDER BY u.created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Cập nhật user
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET email = ?, role = ?, reference_id = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getRole());
            pstmt.setInt(3, user.getReferenceId());
            pstmt.setInt(4, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Đổi password
     */
    public boolean changePassword(int userId, String newPassword) {
        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi đổi password: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa user
     */
    public boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Trích xuất User từ ResultSet
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setReferenceId(rs.getInt("reference_id"));
        user.setFullName(rs.getString("full_name"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
}

