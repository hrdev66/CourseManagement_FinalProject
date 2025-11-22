package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ chính của ứng dụng
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private CoursePanel coursePanel;
    private StudentPanel studentPanel;
    private InstructorPanel instructorPanel;
    private EnrollmentPanel enrollmentPanel;

    public MainFrame() {
        setTitle("Hệ thống Quản lý Khóa học Trực tuyến");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Tạo menu bar
        createMenuBar();

        // Tạo tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Tạo các panel
        coursePanel = new CoursePanel();
        studentPanel = new StudentPanel();
        instructorPanel = new InstructorPanel();
        enrollmentPanel = new EnrollmentPanel();

        // Thêm các tab
        tabbedPane.addTab("📚 Khóa học", coursePanel);
        tabbedPane.addTab("👨‍🎓 Sinh viên", studentPanel);
        tabbedPane.addTab("👨‍🏫 Giảng viên", instructorPanel);
        tabbedPane.addTab("📝 Đăng ký", enrollmentPanel);

        // Thêm tabbed pane vào frame
        add(tabbedPane, BorderLayout.CENTER);

        // Tạo status bar
        createStatusBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu File
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Menu Quản lý
        JMenu manageMenu = new JMenu("Quản lý");
        JMenuItem coursesItem = new JMenuItem("Khóa học");
        coursesItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        JMenuItem studentsItem = new JMenuItem("Sinh viên");
        studentsItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        JMenuItem instructorsItem = new JMenuItem("Giảng viên");
        instructorsItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        JMenuItem enrollmentsItem = new JMenuItem("Đăng ký");
        enrollmentsItem.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        
        manageMenu.add(coursesItem);
        manageMenu.add(studentsItem);
        manageMenu.add(instructorsItem);
        manageMenu.add(enrollmentsItem);

        // Menu Trợ giúp
        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem aboutItem = new JMenuItem("Về chúng tôi");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(manageMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel(" Hệ thống Quản lý Khóa học - Sẵn sàng");
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        add(statusBar, BorderLayout.SOUTH);
    }

    private void showAboutDialog() {
        String message = "Hệ thống Quản lý Khóa học Trực tuyến\n" +
                        "Phiên bản: 1.0\n" +
                        "Dự án cuối kỳ - Java Programming\n\n" +
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

        // Chạy ứng dụng trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}

