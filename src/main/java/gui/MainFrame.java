package gui;

import dao.UserDAO;
import model.User;
import util.PasswordHasher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Cửa sổ chính của ứng dụng (có phân quyền)
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private CoursePanel coursePanel;
    private AssignmentPanel assignmentPanel;
    private AnnouncementPanel announcementPanel;
    private StudentPanel studentPanel;
    private InstructorPanel instructorPanel;
    private EnrollmentPanel enrollmentPanel;
    
    private User currentUser;
    private JLabel userInfoLabel;

    public MainFrame(User user) {
        this.currentUser = user;
        
        setTitle("Hệ thống Quản lý Khóa học Trực tuyến - Canvas LMS Style - " + 
                 user.getFullName() + " (" + user.getRole().toUpperCase() + ")");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        applyPermissions();
        setVisible(true);
    }

    private void initComponents() {
        // Tạo menu bar
        createMenuBar();

        // Tạo tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Tạo các panel
        dashboardPanel = new DashboardPanel();
        coursePanel = new CoursePanel();
        assignmentPanel = new AssignmentPanel();
        announcementPanel = new AnnouncementPanel();
        studentPanel = new StudentPanel();
        instructorPanel = new InstructorPanel();
        enrollmentPanel = new EnrollmentPanel();

        // Thêm các tab (Canvas LMS style)
        tabbedPane.addTab("🏠 Dashboard", dashboardPanel);
        tabbedPane.addTab("📚 Khóa học", coursePanel);
        tabbedPane.addTab("📝 Bài tập", assignmentPanel);
        tabbedPane.addTab("📢 Thông báo", announcementPanel);
        tabbedPane.addTab("👨‍🎓 Sinh viên", studentPanel);
        tabbedPane.addTab("👨‍🏫 Giảng viên", instructorPanel);
        tabbedPane.addTab("📋 Đăng ký", enrollmentPanel);

        // Thêm tabbed pane vào frame
        add(tabbedPane, BorderLayout.CENTER);

        // Tạo status bar
        createStatusBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu File
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("🚪 Đăng xuất");
        logoutItem.addActionListener(e -> logout());
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Menu Quản lý
        JMenu manageMenu = new JMenu("Quản lý");
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        JMenuItem coursesItem = new JMenuItem("Khóa học");
        coursesItem.addActionListener(e -> selectTabByName("📚 Khóa học"));
        JMenuItem assignmentsItem = new JMenuItem("Bài tập");
        assignmentsItem.addActionListener(e -> selectTabByName("📝 Bài tập"));
        JMenuItem announcementsItem = new JMenuItem("Thông báo");
        announcementsItem.addActionListener(e -> selectTabByName("📢 Thông báo"));
        JMenuItem studentsItem = new JMenuItem("Sinh viên");
        studentsItem.addActionListener(e -> selectTabByName("👨‍🎓 Sinh viên"));
        JMenuItem instructorsItem = new JMenuItem("Giảng viên");
        instructorsItem.addActionListener(e -> selectTabByName("👨‍🏫 Giảng viên"));
        JMenuItem enrollmentsItem = new JMenuItem("Đăng ký");
        enrollmentsItem.addActionListener(e -> selectTabByName("📋 Đăng ký"));
        
        manageMenu.add(dashboardItem);
        manageMenu.addSeparator();
        manageMenu.add(coursesItem);
        manageMenu.add(assignmentsItem);
        manageMenu.add(announcementsItem);
        
        // Chỉ admin mới thấy quản lý sinh viên, giảng viên, đăng ký
        if (currentUser.isAdmin()) {
            manageMenu.addSeparator();
            manageMenu.add(studentsItem);
            manageMenu.add(instructorsItem);
            manageMenu.add(enrollmentsItem);
        }

        // Menu Tài khoản
        JMenu accountMenu = new JMenu("Tài khoản");
        JMenuItem changePasswordItem = new JMenuItem("🔑 Đổi mật khẩu");
        changePasswordItem.addActionListener(e -> changePassword());
        JMenuItem profileItem = new JMenuItem("👤 Thông tin tài khoản");
        profileItem.addActionListener(e -> showProfile());
        accountMenu.add(profileItem);
        accountMenu.add(changePasswordItem);

        // Menu Trợ giúp
        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem aboutItem = new JMenuItem("Về chúng tôi");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(manageMenu);
        menuBar.add(accountMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }
    
    private void selectTabByName(String tabName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(tabName)) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void applyPermissions() {
        // Ẩn/hiện tabs theo quyền
        if (currentUser.isStudent()) {
            // Sinh viên chỉ thấy: Dashboard, Khóa học, Bài tập, Thông báo
            // Ẩn các tab quản lý
            hideTab("👨‍🎓 Sinh viên");
            hideTab("👨‍🏫 Giảng viên");
            hideTab("📋 Đăng ký");
        } else if (currentUser.isInstructor()) {
            // Giảng viên thấy: Dashboard, Khóa học (của mình), Bài tập, Thông báo
            // Ẩn quản lý sinh viên và đăng ký
            hideTab("👨‍🎓 Sinh viên");
            hideTab("📋 Đăng ký");
        }
        // Admin thấy tất cả
    }
    
    private void hideTab(String tabName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(tabName)) {
                tabbedPane.removeTabAt(i);
                break;
            }
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn đăng xuất?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
    
    private void changePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, currentUser);
        dialog.setVisible(true);
    }
    
    private void showProfile() {
        String message = String.format(
            "Thông tin tài khoản:\n\n" +
            "Tên đăng nhập: %s\n" +
            "Email: %s\n" +
            "Vai trò: %s\n" +
            "Họ tên: %s\n" +
            "Ngày tạo: %s",
            currentUser.getUsername(),
            currentUser.getEmail(),
            currentUser.getRole().toUpperCase(),
            currentUser.getFullName(),
            currentUser.getCreatedAt() != null ? currentUser.getCreatedAt().toString() : "N/A"
        );
        JOptionPane.showMessageDialog(this, message, "Thông tin tài khoản", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        String roleText = "";
        switch (currentUser.getRole()) {
            case "admin": roleText = "Quản trị viên"; break;
            case "instructor": roleText = "Giảng viên"; break;
            case "student": roleText = "Sinh viên"; break;
        }
        
        JLabel statusLabel = new JLabel(" Hệ thống Quản lý Khóa học - Canvas LMS Style - Sẵn sàng");
        userInfoLabel = new JLabel(" 👤 " + currentUser.getFullName() + " (" + roleText + ")");
        userInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(userInfoLabel, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
    }

    private void showAboutDialog() {
        String message = "Hệ thống Quản lý Khóa học Trực tuyến\n" +
                        "Phiên bản: 2.0 (Canvas LMS Style)\n" +
                        "Dự án cuối kỳ - Java Programming\n\n" +
                        "Tính năng:\n" +
                        "• Dashboard tổng quan\n" +
                        "• Quản lý Bài tập\n" +
                        "• Thông báo\n" +
                        "• Theo dõi tiến độ\n\n" +
                        "Sử dụng: Java + JDBC + Swing";
        JOptionPane.showMessageDialog(this, message, "Về chúng tôi", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Sử dụng Look and Feel của hệ thống
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy LoginFrame trước
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}

/**
 * Dialog đổi mật khẩu
 */
class ChangePasswordDialog extends JDialog {
    private JPasswordField oldPasswordField, newPasswordField, confirmPasswordField;
    private JButton saveButton, cancelButton;
    private User currentUser;
    private UserDAO userDAO;

    public ChangePasswordDialog(Frame parent, User user) {
        super(parent, "Đổi mật khẩu", true);
        this.currentUser = user;
        this.userDAO = new UserDAO();
        
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Old Password
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Mật khẩu cũ:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        oldPasswordField = new JPasswordField(20);
        formPanel.add(oldPasswordField, gbc);

        // New Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        formPanel.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Xác nhận mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("💾 Lưu");
        saveButton.addActionListener(e -> performChangePassword());
        cancelButton = new JButton("❌ Hủy");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void performChangePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // Kiểm tra mật khẩu cũ
        if (!PasswordHasher.verifyPassword(oldPassword, currentUser.getPassword())) {
            JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng!");
            return;
        }

        // Kiểm tra mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới xác nhận không khớp!");
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return;
        }

        // Đổi mật khẩu
        if (userDAO.changePassword(currentUser.getUserId(), newPassword)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", 
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi đổi mật khẩu!", 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

