package gui;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel Dashboard - Tổng quan hệ thống (giống Canvas LMS)
 */
public class DashboardPanel extends JPanel {
    private JPanel statsPanel;
    private JTable coursesTable, assignmentsTable, announcementsTable;
    private DefaultTableModel coursesModel, assignmentsModel, announcementsModel;
    
    private CourseDAO courseDAO;
    private AssignmentDAO assignmentDAO;
    private AnnouncementDAO announcementDAO;
    private EnrollmentDAO enrollmentDAO;

    public DashboardPanel() {
        courseDAO = new CourseDAO();
        assignmentDAO = new AssignmentDAO();
        announcementDAO = new AnnouncementDAO();
        enrollmentDAO = new EnrollmentDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // Panel thống kê ở trên
        createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);

        // Panel chính với tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab Khóa học của tôi
        JScrollPane coursesScroll = createCoursesTable();
        tabbedPane.addTab("📚 Khóa học của tôi", coursesScroll);
        
        // Tab Bài tập gần đến hạn
        JScrollPane assignmentsScroll = createAssignmentsTable();
        tabbedPane.addTab("📝 Bài tập sắp đến hạn", assignmentsScroll);
        
        // Tab Thông báo mới
        JScrollPane announcementsScroll = createAnnouncementsTable();
        tabbedPane.addTab("📢 Thông báo mới", announcementsScroll);
        
        add(tabbedPane, BorderLayout.CENTER);

        // Nút refresh
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.addActionListener(e -> loadData());
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createStatsPanel() {
        statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        
        // Thống kê số khóa học
        JPanel coursesStat = createStatCard("Khóa học đã đăng ký", "0", Color.BLUE);
        statsPanel.add(coursesStat);
        
        // Thống kê bài tập
        JPanel assignmentsStat = createStatCard("Bài tập chưa nộp", "0", Color.ORANGE);
        statsPanel.add(assignmentsStat);
        
        // Thống kê thông báo
        JPanel announcementsStat = createStatCard("Thông báo mới", "0", Color.GREEN);
        statsPanel.add(announcementsStat);
        
        // Thống kê tiến độ
        JPanel progressStat = createStatCard("Tiến độ trung bình", "0%", new Color(128, 0, 128)); // Purple color
        statsPanel.add(progressStat);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setName("statValue"); // Để cập nhật sau
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private JScrollPane createCoursesTable() {
        String[] columnNames = {"ID", "Mã khóa học", "Tên khóa học", "Giảng viên", "Trạng thái", "Điểm"};
        coursesModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        coursesTable = new JTable(coursesModel);
        coursesTable.setRowHeight(25);
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        return new JScrollPane(coursesTable);
    }

    private JScrollPane createAssignmentsTable() {
        String[] columnNames = {"ID", "Khóa học", "Bài tập", "Hạn nộp", "Điểm tối đa", "Trạng thái"};
        assignmentsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignmentsTable = new JTable(assignmentsModel);
        assignmentsTable.setRowHeight(25);
        assignmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        return new JScrollPane(assignmentsTable);
    }

    private JScrollPane createAnnouncementsTable() {
        String[] columnNames = {"ID", "Khóa học", "Tiêu đề", "Giảng viên", "Độ ưu tiên", "Ngày đăng"};
        announcementsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        announcementsTable = new JTable(announcementsModel);
        announcementsTable.setRowHeight(25);
        announcementsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        return new JScrollPane(announcementsTable);
    }

    private void loadData() {
        loadCourses();
        loadAssignments();
        loadAnnouncements();
        updateStats();
    }

    private void loadCourses() {
        coursesModel.setRowCount(0);
        
        // Lấy tất cả đăng ký (trong thực tế, lọc theo user đang đăng nhập)
        List<Enrollment> enrollments = enrollmentDAO.findAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Enrollment enrollment : enrollments) {
            Course course = courseDAO.findById(enrollment.getCourseId());
            if (course != null) {
                Object[] row = {
                    course.getCourseId(),
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getInstructorName(),
                    enrollment.getCompletionStatus(),
                    enrollment.getGrade() != null ? String.format("%.2f", enrollment.getGrade()) : "Chưa có"
                };
                coursesModel.addRow(row);
            }
        }
    }

    private void loadAssignments() {
        assignmentsModel.setRowCount(0);
        
        // Lấy bài tập sắp đến hạn (7 ngày tới)
        List<Assignment> assignments = assignmentDAO.findAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Assignment assignment : assignments) {
            if (assignment.getDueDate() != null) {
                Object[] row = {
                    assignment.getAssignmentId(),
                    assignment.getCourseName(),
                    assignment.getTitle(),
                    sdf.format(assignment.getDueDate()),
                    assignment.getMaxScore(),
                    assignment.getStatus()
                };
                assignmentsModel.addRow(row);
            }
        }
    }

    private void loadAnnouncements() {
        announcementsModel.setRowCount(0);
        
        List<Announcement> announcements = announcementDAO.findAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        // Chỉ lấy 10 thông báo mới nhất
        int count = 0;
        for (Announcement announcement : announcements) {
            if (count >= 10) break;
            
            String priorityText = "";
            switch (announcement.getPriority()) {
                case "urgent": priorityText = "🔴 Khẩn cấp"; break;
                case "important": priorityText = "🟡 Quan trọng"; break;
                default: priorityText = "⚪ Bình thường"; break;
            }
            
            Object[] row = {
                announcement.getAnnouncementId(),
                announcement.getCourseName(),
                announcement.getTitle(),
                announcement.getInstructorName(),
                priorityText,
                sdf.format(announcement.getCreatedAt())
            };
            announcementsModel.addRow(row);
            count++;
        }
    }

    private void updateStats() {
        // Cập nhật thống kê
        int enrolledCourses = coursesModel.getRowCount();
        int pendingAssignments = assignmentsModel.getRowCount();
        int newAnnouncements = announcementsModel.getRowCount();
        
        // Tính tiến độ trung bình
        double totalProgress = 0;
        int completedCount = 0;
        List<Enrollment> enrollments = enrollmentDAO.findAll();
        for (Enrollment e : enrollments) {
            if ("completed".equals(e.getCompletionStatus())) {
                completedCount++;
            }
        }
        int totalEnrollments = enrollments.size();
        double avgProgress = totalEnrollments > 0 ? (completedCount * 100.0 / totalEnrollments) : 0;
        
        // Cập nhật labels
        updateStatValue(statsPanel, 0, String.valueOf(enrolledCourses));
        updateStatValue(statsPanel, 1, String.valueOf(pendingAssignments));
        updateStatValue(statsPanel, 2, String.valueOf(newAnnouncements));
        updateStatValue(statsPanel, 3, String.format("%.0f%%", avgProgress));
    }

    private void updateStatValue(JPanel panel, int index, String value) {
        if (index < panel.getComponentCount()) {
            JPanel card = (JPanel) panel.getComponent(index);
            for (Component comp : card.getComponents()) {
                if (comp instanceof JLabel && "statValue".equals(((JLabel) comp).getName())) {
                    ((JLabel) comp).setText(value);
                    break;
                }
            }
        }
    }
}

