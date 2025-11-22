package gui;

import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import model.Course;
import model.Enrollment;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel quản lý đăng ký khóa học
 */
public class EnrollmentPanel extends JPanel {
    private JTable enrollmentTable;
    private DefaultTableModel tableModel;
    private EnrollmentDAO enrollmentDAO;
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;

    public EnrollmentPanel() {
        enrollmentDAO = new EnrollmentDAO();
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadEnrollments();
    }

    private void initComponents() {
        // Panel nút chức năng
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Đăng ký mới");
        addButton.addActionListener(e -> addEnrollment());
        JButton editButton = new JButton("✏️ Cập nhật");
        editButton.addActionListener(e -> editEnrollment());
        JButton deleteButton = new JButton("🗑️ Xóa");
        deleteButton.addActionListener(e -> deleteEnrollment());
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.addActionListener(e -> loadEnrollments());
        
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(refreshButton);
        
        add(topPanel, BorderLayout.NORTH);

        // Bảng đăng ký
        String[] columnNames = {"ID", "Sinh viên", "Khóa học", "Ngày đăng ký", 
                               "Trạng thái học", "Điểm", "Thanh toán"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollmentTable = new JTable(tableModel);
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enrollmentTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadEnrollments() {
        tableModel.setRowCount(0);
        List<Enrollment> enrollments = enrollmentDAO.findAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Enrollment enrollment : enrollments) {
            Object[] row = {
                enrollment.getEnrollmentId(),
                enrollment.getStudentName(),
                enrollment.getCourseName(),
                enrollment.getEnrollmentDate() != null ? sdf.format(enrollment.getEnrollmentDate()) : "",
                enrollment.getCompletionStatus(),
                enrollment.getGrade() != null ? String.format("%.2f", enrollment.getGrade()) : "Chưa có",
                enrollment.getPaymentStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void addEnrollment() {
        EnrollmentDialog dialog = new EnrollmentDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                       "Đăng ký khóa học mới", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Enrollment enrollment = dialog.getEnrollment();
            
            // Kiểm tra đã đăng ký chưa
            if (enrollmentDAO.isEnrolled(enrollment.getStudentId(), enrollment.getCourseId())) {
                JOptionPane.showMessageDialog(this, "Sinh viên đã đăng ký khóa học này rồi!", 
                                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (enrollmentDAO.insert(enrollment)) {
                JOptionPane.showMessageDialog(this, "Đăng ký khóa học thành công!");
                loadEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi đăng ký khóa học!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đăng ký cần cập nhật!");
            return;
        }

        int enrollmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Enrollment enrollment = enrollmentDAO.findById(enrollmentId);
        
        EnrollmentDialog dialog = new EnrollmentDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                       "Cập nhật đăng ký", enrollment);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Enrollment updatedEnrollment = dialog.getEnrollment();
            updatedEnrollment.setEnrollmentId(enrollmentId);
            if (enrollmentDAO.update(updatedEnrollment)) {
                JOptionPane.showMessageDialog(this, "Cập nhật đăng ký thành công!");
                loadEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật đăng ký!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đăng ký cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa đăng ký này?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int enrollmentId = (int) tableModel.getValueAt(selectedRow, 0);
            if (enrollmentDAO.delete(enrollmentId)) {
                JOptionPane.showMessageDialog(this, "Xóa đăng ký thành công!");
                loadEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa đăng ký!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

/**
 * Dialog thêm/sửa đăng ký
 */
class EnrollmentDialog extends JDialog {
    private JComboBox<Student> studentCombo;
    private JComboBox<Course> courseCombo;
    private JTextField enrollmentDateField, gradeField;
    private JComboBox<String> statusCombo, paymentCombo;
    private boolean confirmed = false;
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;

    public EnrollmentDialog(Frame parent, String title, Enrollment enrollment) {
        super(parent, title, true);
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();
        
        setSize(450, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Sinh viên
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Sinh viên:"), gbc);
        gbc.gridx = 1;
        studentCombo = new JComboBox<>();
        loadStudents();
        formPanel.add(studentCombo, gbc);

        // Khóa học
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Khóa học:"), gbc);
        gbc.gridx = 1;
        courseCombo = new JComboBox<>();
        loadCourses();
        formPanel.add(courseCombo, gbc);

        // Ngày đăng ký
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Ngày đăng ký (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        enrollmentDateField = new JTextField(20);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        enrollmentDateField.setText(sdf.format(new java.util.Date()));
        formPanel.add(enrollmentDateField, gbc);

        // Trạng thái học
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Trạng thái học:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"enrolled", "in_progress", "completed", "dropped"});
        formPanel.add(statusCombo, gbc);

        // Điểm
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Điểm (để trống nếu chưa có):"), gbc);
        gbc.gridx = 1;
        gradeField = new JTextField(20);
        formPanel.add(gradeField, gbc);

        // Thanh toán
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Thanh toán:"), gbc);
        gbc.gridx = 1;
        paymentCombo = new JComboBox<>(new String[]{"pending", "paid", "refunded"});
        formPanel.add(paymentCombo, gbc);

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

        // Nếu đang sửa, điền dữ liệu
        if (enrollment != null) {
            fillData(enrollment);
        }
    }

    private void loadStudents() {
        List<Student> students = studentDAO.findAll();
        for (Student student : students) {
            studentCombo.addItem(student);
        }
    }

    private void loadCourses() {
        List<Course> courses = courseDAO.findAll();
        for (Course course : courses) {
            courseCombo.addItem(course);
        }
    }

    private void fillData(Enrollment enrollment) {
        // Chọn sinh viên
        for (int i = 0; i < studentCombo.getItemCount(); i++) {
            if (studentCombo.getItemAt(i).getStudentId() == enrollment.getStudentId()) {
                studentCombo.setSelectedIndex(i);
                break;
            }
        }
        
        // Chọn khóa học
        for (int i = 0; i < courseCombo.getItemCount(); i++) {
            if (courseCombo.getItemAt(i).getCourseId() == enrollment.getCourseId()) {
                courseCombo.setSelectedIndex(i);
                break;
            }
        }
        
        if (enrollment.getEnrollmentDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            enrollmentDateField.setText(sdf.format(enrollment.getEnrollmentDate()));
        }
        
        statusCombo.setSelectedItem(enrollment.getCompletionStatus());
        
        if (enrollment.getGrade() != null) {
            gradeField.setText(String.valueOf(enrollment.getGrade()));
        }
        
        paymentCombo.setSelectedItem(enrollment.getPaymentStatus());
    }

    private boolean validateInput() {
        if (!enrollmentDateField.getText().trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                sdf.parse(enrollmentDateField.getText().trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ngày đăng ký không hợp lệ! (dd/MM/yyyy)");
                return false;
            }
        }
        
        if (!gradeField.getText().trim().isEmpty()) {
            try {
                double grade = Double.parseDouble(gradeField.getText().trim());
                if (grade < 0 || grade > 10) {
                    JOptionPane.showMessageDialog(this, "Điểm phải từ 0 đến 10!");
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Điểm không hợp lệ!");
                return false;
            }
        }
        
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Enrollment getEnrollment() {
        Student selectedStudent = (Student) studentCombo.getSelectedItem();
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        
        Date enrollDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date parsed = sdf.parse(enrollmentDateField.getText().trim());
            enrollDate = new Date(parsed.getTime());
        } catch (Exception e) {
            enrollDate = new Date(System.currentTimeMillis());
        }
        
        Double grade = null;
        if (!gradeField.getText().trim().isEmpty()) {
            grade = Double.parseDouble(gradeField.getText().trim());
        }
        
        return new Enrollment(
            selectedStudent.getStudentId(),
            selectedCourse.getCourseId(),
            enrollDate,
            (String) statusCombo.getSelectedItem(),
            grade,
            (String) paymentCombo.getSelectedItem()
        );
    }
}

