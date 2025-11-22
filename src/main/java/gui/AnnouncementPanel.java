package gui;

import dao.AnnouncementDAO;
import dao.CourseDAO;
import dao.InstructorDAO;
import model.Announcement;
import model.Course;
import model.Instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel quản lý Thông báo (giống Canvas LMS)
 */
public class AnnouncementPanel extends JPanel {
    private JTable announcementTable;
    private DefaultTableModel tableModel;
    private AnnouncementDAO announcementDAO;
    private CourseDAO courseDAO;
    private InstructorDAO instructorDAO;

    public AnnouncementPanel() {
        announcementDAO = new AnnouncementDAO();
        courseDAO = new CourseDAO();
        instructorDAO = new InstructorDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadAnnouncements();
    }

    private void initComponents() {
        // Panel nút chức năng
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Thêm thông báo");
        addButton.addActionListener(e -> addAnnouncement());
        JButton editButton = new JButton("✏️ Sửa");
        editButton.addActionListener(e -> editAnnouncement());
        JButton deleteButton = new JButton("🗑️ Xóa");
        deleteButton.addActionListener(e -> deleteAnnouncement());
        JButton viewButton = new JButton("👁️ Xem chi tiết");
        viewButton.addActionListener(e -> viewAnnouncement());
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.addActionListener(e -> loadAnnouncements());
        
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(viewButton);
        topPanel.add(refreshButton);
        
        add(topPanel, BorderLayout.NORTH);

        // Bảng thông báo
        String[] columnNames = {"ID", "Khóa học", "Tiêu đề", "Giảng viên", "Độ ưu tiên", "Ngày đăng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        announcementTable = new JTable(tableModel);
        announcementTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        announcementTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(announcementTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadAnnouncements() {
        tableModel.setRowCount(0);
        List<Announcement> announcements = announcementDAO.findAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Announcement announcement : announcements) {
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
            tableModel.addRow(row);
        }
    }

    private void addAnnouncement() {
        AnnouncementDialog dialog = new AnnouncementDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                            "Thêm thông báo mới", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Announcement announcement = dialog.getAnnouncement();
            if (announcementDAO.insert(announcement)) {
                JOptionPane.showMessageDialog(this, "Thêm thông báo thành công!");
                loadAnnouncements();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm thông báo!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editAnnouncement() {
        int selectedRow = announcementTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thông báo cần sửa!");
            return;
        }

        int announcementId = (int) tableModel.getValueAt(selectedRow, 0);
        Announcement announcement = announcementDAO.findById(announcementId);
        
        AnnouncementDialog dialog = new AnnouncementDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                            "Sửa thông báo", announcement);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Announcement updatedAnnouncement = dialog.getAnnouncement();
            updatedAnnouncement.setAnnouncementId(announcementId);
            if (announcementDAO.update(updatedAnnouncement)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông báo thành công!");
                loadAnnouncements();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật thông báo!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteAnnouncement() {
        int selectedRow = announcementTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thông báo cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa thông báo này?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int announcementId = (int) tableModel.getValueAt(selectedRow, 0);
            if (announcementDAO.delete(announcementId)) {
                JOptionPane.showMessageDialog(this, "Xóa thông báo thành công!");
                loadAnnouncements();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa thông báo!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewAnnouncement() {
        int selectedRow = announcementTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thông báo để xem!");
            return;
        }

        int announcementId = (int) tableModel.getValueAt(selectedRow, 0);
        Announcement announcement = announcementDAO.findById(announcementId);
        
        if (announcement != null) {
            String message = String.format(
                "Tiêu đề: %s\n\n" +
                "Khóa học: %s\n" +
                "Giảng viên: %s\n" +
                "Độ ưu tiên: %s\n\n" +
                "Nội dung:\n%s",
                announcement.getTitle(),
                announcement.getCourseName(),
                announcement.getInstructorName(),
                announcement.getPriority(),
                announcement.getContent()
            );
            JOptionPane.showMessageDialog(this, message, "Chi tiết thông báo", 
                                        JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

/**
 * Dialog thêm/sửa thông báo
 */
class AnnouncementDialog extends JDialog {
    private JComboBox<Course> courseCombo;
    private JComboBox<Instructor> instructorCombo;
    private JTextField titleField;
    private JTextArea contentArea;
    private JComboBox<String> priorityCombo;
    private boolean confirmed = false;
    private CourseDAO courseDAO;
    private InstructorDAO instructorDAO;

    public AnnouncementDialog(Frame parent, String title, Announcement announcement) {
        super(parent, title, true);
        courseDAO = new CourseDAO();
        instructorDAO = new InstructorDAO();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Khóa học
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Khóa học:"), gbc);
        gbc.gridx = 1;
        courseCombo = new JComboBox<>();
        loadCourses();
        formPanel.add(courseCombo, gbc);

        // Giảng viên
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Giảng viên:"), gbc);
        gbc.gridx = 1;
        instructorCombo = new JComboBox<>();
        loadInstructors();
        formPanel.add(instructorCombo, gbc);

        // Tiêu đề
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);

        // Nội dung
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Nội dung:"), gbc);
        gbc.gridx = 1;
        contentArea = new JTextArea(8, 30);
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        formPanel.add(contentScroll, gbc);

        // Độ ưu tiên
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Độ ưu tiên:"), gbc);
        gbc.gridx = 1;
        priorityCombo = new JComboBox<>(new String[]{"normal", "important", "urgent"});
        formPanel.add(priorityCombo, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("💾 Lưu");
        saveButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });
        JButton cancelButton = new JButton("❌ Hủy");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (announcement != null) {
            fillData(announcement);
        }
    }

    private void loadCourses() {
        List<Course> courses = courseDAO.findAll();
        for (Course course : courses) {
            courseCombo.addItem(course);
        }
    }

    private void loadInstructors() {
        List<Instructor> instructors = instructorDAO.findAll();
        for (Instructor instructor : instructors) {
            instructorCombo.addItem(instructor);
        }
    }

    private void fillData(Announcement announcement) {
        for (int i = 0; i < courseCombo.getItemCount(); i++) {
            if (courseCombo.getItemAt(i).getCourseId() == announcement.getCourseId()) {
                courseCombo.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < instructorCombo.getItemCount(); i++) {
            if (instructorCombo.getItemAt(i).getInstructorId() == announcement.getInstructorId()) {
                instructorCombo.setSelectedIndex(i);
                break;
            }
        }
        titleField.setText(announcement.getTitle());
        contentArea.setText(announcement.getContent());
        priorityCombo.setSelectedItem(announcement.getPriority());
    }

    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiêu đề!");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Announcement getAnnouncement() {
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        Instructor selectedInstructor = (Instructor) instructorCombo.getSelectedItem();
        
        return new Announcement(
            selectedCourse.getCourseId(),
            selectedInstructor.getInstructorId(),
            titleField.getText().trim(),
            contentArea.getText().trim(),
            (String) priorityCombo.getSelectedItem()
        );
    }
}

