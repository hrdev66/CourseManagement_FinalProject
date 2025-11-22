package gui;

import dao.AssignmentDAO;
import dao.CourseDAO;
import dao.SubmissionDAO;
import model.Assignment;
import model.Course;
import model.Submission;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel quản lý Bài tập (giống Canvas LMS)
 */
public class AssignmentPanel extends JPanel {
    private JTable assignmentTable;
    private DefaultTableModel tableModel;
    private AssignmentDAO assignmentDAO;
    private CourseDAO courseDAO;
    private SubmissionDAO submissionDAO;

    public AssignmentPanel() {
        assignmentDAO = new AssignmentDAO();
        courseDAO = new CourseDAO();
        submissionDAO = new SubmissionDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadAssignments();
    }

    private void initComponents() {
        // Panel nút chức năng
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Thêm bài tập");
        addButton.addActionListener(e -> addAssignment());
        JButton editButton = new JButton("✏️ Sửa");
        editButton.addActionListener(e -> editAssignment());
        JButton deleteButton = new JButton("🗑️ Xóa");
        deleteButton.addActionListener(e -> deleteAssignment());
        JButton viewSubmissionsButton = new JButton("📥 Xem bài nộp");
        viewSubmissionsButton.addActionListener(e -> viewSubmissions());
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.addActionListener(e -> loadAssignments());
        
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(viewSubmissionsButton);
        topPanel.add(refreshButton);
        
        add(topPanel, BorderLayout.NORTH);

        // Bảng bài tập
        String[] columnNames = {"ID", "Khóa học", "Tiêu đề", "Hạn nộp", "Điểm tối đa", "Loại", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignmentTable = new JTable(tableModel);
        assignmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignmentTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(assignmentTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadAssignments() {
        tableModel.setRowCount(0);
        List<Assignment> assignments = assignmentDAO.findAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Assignment assignment : assignments) {
            Object[] row = {
                assignment.getAssignmentId(),
                assignment.getCourseName(),
                assignment.getTitle(),
                assignment.getDueDate() != null ? sdf.format(assignment.getDueDate()) : "Không có",
                assignment.getMaxScore(),
                assignment.getAssignmentType(),
                assignment.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void addAssignment() {
        AssignmentDialog dialog = new AssignmentDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                      "Thêm bài tập mới", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Assignment assignment = dialog.getAssignment();
            if (assignmentDAO.insert(assignment)) {
                JOptionPane.showMessageDialog(this, "Thêm bài tập thành công!");
                loadAssignments();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm bài tập!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editAssignment() {
        int selectedRow = assignmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài tập cần sửa!");
            return;
        }

        int assignmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Assignment assignment = assignmentDAO.findById(assignmentId);
        
        AssignmentDialog dialog = new AssignmentDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                      "Sửa bài tập", assignment);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Assignment updatedAssignment = dialog.getAssignment();
            updatedAssignment.setAssignmentId(assignmentId);
            if (assignmentDAO.update(updatedAssignment)) {
                JOptionPane.showMessageDialog(this, "Cập nhật bài tập thành công!");
                loadAssignments();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật bài tập!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteAssignment() {
        int selectedRow = assignmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài tập cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa bài tập này?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int assignmentId = (int) tableModel.getValueAt(selectedRow, 0);
            if (assignmentDAO.delete(assignmentId)) {
                JOptionPane.showMessageDialog(this, "Xóa bài tập thành công!");
                loadAssignments();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa bài tập!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSubmissions() {
        int selectedRow = assignmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài tập để xem bài nộp!");
            return;
        }

        int assignmentId = (int) tableModel.getValueAt(selectedRow, 0);
        List<Submission> submissions = submissionDAO.findByAssignment(assignmentId);
        
        SubmissionViewDialog dialog = new SubmissionViewDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            "Bài nộp", assignmentId, submissions
        );
        dialog.setVisible(true);
    }
}

/**
 * Dialog thêm/sửa bài tập
 */
class AssignmentDialog extends JDialog {
    private JComboBox<Course> courseCombo;
    private JTextField titleField, maxScoreField;
    private JTextArea descriptionArea;
    private JTextField dueDateField;
    private JComboBox<String> typeCombo, statusCombo;
    private boolean confirmed = false;
    private CourseDAO courseDAO;

    public AssignmentDialog(Frame parent, String title, Assignment assignment) {
        super(parent, title, true);
        courseDAO = new CourseDAO();
        
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

        // Tiêu đề
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);

        // Mô tả
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);

        // Hạn nộp
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Hạn nộp (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        dueDateField = new JTextField(30);
        formPanel.add(dueDateField, gbc);

        // Điểm tối đa
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Điểm tối đa:"), gbc);
        gbc.gridx = 1;
        maxScoreField = new JTextField(30);
        maxScoreField.setText("100");
        formPanel.add(maxScoreField, gbc);

        // Loại
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Loại:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"homework", "quiz", "project"});
        formPanel.add(typeCombo, gbc);

        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"published", "draft"});
        formPanel.add(statusCombo, gbc);

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

        if (assignment != null) {
            fillData(assignment);
        }
    }

    private void loadCourses() {
        List<Course> courses = courseDAO.findAll();
        for (Course course : courses) {
            courseCombo.addItem(course);
        }
    }

    private void fillData(Assignment assignment) {
        for (int i = 0; i < courseCombo.getItemCount(); i++) {
            if (courseCombo.getItemAt(i).getCourseId() == assignment.getCourseId()) {
                courseCombo.setSelectedIndex(i);
                break;
            }
        }
        titleField.setText(assignment.getTitle());
        descriptionArea.setText(assignment.getDescription());
        if (assignment.getDueDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dueDateField.setText(sdf.format(assignment.getDueDate()));
        }
        maxScoreField.setText(String.valueOf(assignment.getMaxScore()));
        typeCombo.setSelectedItem(assignment.getAssignmentType());
        statusCombo.setSelectedItem(assignment.getStatus());
    }

    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiêu đề!");
            return false;
        }
        try {
            Integer.parseInt(maxScoreField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm tối đa phải là số!");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Assignment getAssignment() {
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        Date dueDate = null;
        try {
            if (!dueDateField.getText().trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date parsed = sdf.parse(dueDateField.getText().trim());
                dueDate = new Date(parsed.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new Assignment(
            selectedCourse.getCourseId(),
            titleField.getText().trim(),
            descriptionArea.getText().trim(),
            dueDate,
            Integer.parseInt(maxScoreField.getText().trim()),
            (String) typeCombo.getSelectedItem(),
            (String) statusCombo.getSelectedItem()
        );
    }
}

/**
 * Dialog xem bài nộp
 */
class SubmissionViewDialog extends JDialog {
    private JTable submissionTable;
    private DefaultTableModel tableModel;
    private List<Submission> submissions;
    private SubmissionDAO submissionDAO;

    public SubmissionViewDialog(Frame parent, String title, int assignmentId, List<Submission> submissions) {
        super(parent, title, true);
        this.submissions = submissions;
        submissionDAO = new SubmissionDAO();
        
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] columnNames = {"ID", "Sinh viên", "Nội dung", "Điểm", "Trạng thái", "Ngày nộp"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Chỉ cột điểm có thể edit
            }
        };
        submissionTable = new JTable(tableModel);
        submissionTable.setRowHeight(25);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Submission submission : submissions) {
            Object[] row = {
                submission.getSubmissionId(),
                submission.getStudentName(),
                submission.getContent() != null && submission.getContent().length() > 50 
                    ? submission.getContent().substring(0, 50) + "..." 
                    : submission.getContent(),
                submission.getScore() != null ? submission.getScore() : "Chưa chấm",
                submission.getStatus(),
                submission.getSubmittedDate() != null ? sdf.format(submission.getSubmittedDate()) : ""
            };
            tableModel.addRow(row);
        }
        
        JScrollPane scrollPane = new JScrollPane(submissionTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton gradeButton = new JButton("✅ Chấm điểm");
        gradeButton.addActionListener(e -> gradeSubmission());
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(gradeButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void gradeSubmission() {
        int selectedRow = submissionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài nộp để chấm điểm!");
            return;
        }

        int submissionId = (int) tableModel.getValueAt(selectedRow, 0);
        String scoreStr = JOptionPane.showInputDialog(this, "Nhập điểm:", 
                                                     tableModel.getValueAt(selectedRow, 3));
        
        if (scoreStr != null && !scoreStr.trim().isEmpty()) {
            try {
                int score = Integer.parseInt(scoreStr.trim());
                Submission submission = submissionDAO.findById(submissionId);
                if (submission != null) {
                    submission.setScore(score);
                    submission.setStatus("graded");
                    if (submissionDAO.update(submission)) {
                        JOptionPane.showMessageDialog(this, "Chấm điểm thành công!");
                        tableModel.setValueAt(score, selectedRow, 3);
                        tableModel.setValueAt("graded", selectedRow, 4);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Điểm phải là số!");
            }
        }
    }
}

