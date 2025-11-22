package dao;

import database.DatabaseConnection;
import model.Announcement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Announcement
 */
public class AnnouncementDAO {
    private Connection connection;

    public AnnouncementDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Thêm thông báo mới
     */
    public boolean insert(Announcement announcement) {
        String sql = "INSERT INTO announcements (course_id, instructor_id, title, content, priority) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, announcement.getCourseId());
            pstmt.setInt(2, announcement.getInstructorId());
            pstmt.setString(3, announcement.getTitle());
            pstmt.setString(4, announcement.getContent());
            pstmt.setString(5, announcement.getPriority());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    announcement.setAnnouncementId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm thông báo: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật thông báo
     */
    public boolean update(Announcement announcement) {
        String sql = "UPDATE announcements SET course_id = ?, title = ?, content = ?, priority = ? " +
                     "WHERE announcement_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, announcement.getCourseId());
            pstmt.setString(2, announcement.getTitle());
            pstmt.setString(3, announcement.getContent());
            pstmt.setString(4, announcement.getPriority());
            pstmt.setInt(5, announcement.getAnnouncementId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật thông báo: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa thông báo
     */
    public boolean delete(int announcementId) {
        String sql = "DELETE FROM announcements WHERE announcement_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, announcementId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa thông báo: " + e.getMessage());
        }
        return false;
    }

    /**
     * Tìm thông báo theo ID
     */
    public Announcement findById(int announcementId) {
        String sql = "SELECT a.*, c.course_name, i.full_name as instructor_name " +
                     "FROM announcements a " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "JOIN instructors i ON a.instructor_id = i.instructor_id " +
                     "WHERE a.announcement_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, announcementId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractAnnouncementFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm thông báo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy tất cả thông báo
     */
    public List<Announcement> findAll() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name, i.full_name as instructor_name " +
                     "FROM announcements a " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "JOIN instructors i ON a.instructor_id = i.instructor_id " +
                     "ORDER BY a.created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách thông báo: " + e.getMessage());
        }
        return announcements;
    }

    /**
     * Lấy thông báo theo khóa học
     */
    public List<Announcement> findByCourse(int courseId) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name, i.full_name as instructor_name " +
                     "FROM announcements a " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "JOIN instructors i ON a.instructor_id = i.instructor_id " +
                     "WHERE a.course_id = ? ORDER BY a.created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm thông báo theo khóa học: " + e.getMessage());
        }
        return announcements;
    }

    /**
     * Trích xuất Announcement từ ResultSet
     */
    private Announcement extractAnnouncementFromResultSet(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(rs.getInt("announcement_id"));
        announcement.setCourseId(rs.getInt("course_id"));
        announcement.setCourseName(rs.getString("course_name"));
        announcement.setInstructorId(rs.getInt("instructor_id"));
        announcement.setInstructorName(rs.getString("instructor_name"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));
        announcement.setPriority(rs.getString("priority"));
        announcement.setCreatedAt(rs.getTimestamp("created_at"));
        return announcement;
    }
}

