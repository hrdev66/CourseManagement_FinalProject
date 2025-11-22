package gui;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel quản lý sinh viên
 */
public class StudentPanel extends JPanel {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    private JTextField searchField;

    public StudentPanel() {
        studentDAO = new StudentDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadStudents();
    }

    private void initComponents() {
        // Panel tìm kiếm và nút
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("🔍 Tìm");
        searchButton.addActionListener(e -> searchStudents());
        searchPanel.add(searchButton);
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.addActionListener(e -> loadStudents());
        searchPanel.add(refreshButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Thêm");
        addButton.addActionListener(e -> addStudent());
        JButton editButton = new JButton("✏️ Sửa");
        editButton.addActionListener(e -> editStudent());
        JButton deleteButton = new JButton("🗑️ Xóa");
        deleteButton.addActionListener(e -> deleteStudent());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // Bảng sinh viên
        String[] columnNames = {"ID", "Họ tên", "Email", "Số điện thoại", 
                               "Ngày sinh", "Địa chỉ", "Ngày đăng ký"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.findAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Student student : students) {
            Object[] row = {
                student.getStudentId(),
                student.getFullName(),
                student.getEmail(),
                student.getPhone(),
                student.getDateOfBirth() != null ? sdf.format(student.getDateOfBirth()) : "",
                student.getAddress(),
                student.getEnrollmentDate() != null ? sdf.format(student.getEnrollmentDate()) : ""
            };
            tableModel.addRow(row);
        }
    }

    private void searchStudents() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadStudents();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.search(keyword);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Student student : students) {
            Object[] row = {
                student.getStudentId(),
                student.getFullName(),
                student.getEmail(),
                student.getPhone(),
                student.getDateOfBirth() != null ? sdf.format(student.getDateOfBirth()) : "",
                student.getAddress(),
                student.getEnrollmentDate() != null ? sdf.format(student.getEnrollmentDate()) : ""
            };
            tableModel.addRow(row);
        }
    }

    private void addStudent() {
        StudentDialog dialog = new StudentDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                 "Thêm sinh viên mới", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Student student = dialog.getStudent();
            if (studentDAO.insert(student)) {
                JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm sinh viên!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần sửa!");
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        Student student = studentDAO.findById(studentId);
        
        StudentDialog dialog = new StudentDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                 "Sửa thông tin sinh viên", student);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Student updatedStudent = dialog.getStudent();
            updatedStudent.setStudentId(studentId);
            if (studentDAO.update(updatedStudent)) {
                JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thành công!");
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật sinh viên!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa sinh viên này?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int studentId = (int) tableModel.getValueAt(selectedRow, 0);
            if (studentDAO.delete(studentId)) {
                JOptionPane.showMessageDialog(this, "Xóa sinh viên thành công!");
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa sinh viên!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

/**
 * Dialog thêm/sửa sinh viên
 */
class StudentDialog extends JDialog {
    private JTextField nameField, emailField, phoneField, dobField, addressField;
    private boolean confirmed = false;

    public StudentDialog(Frame parent, String title, Student student) {
        super(parent, title, true);
        
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Họ tên
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày sinh (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        dobField = new JTextField(20);
        formPanel.add(dobField, gbc);

        // Địa chỉ
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

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
        if (student != null) {
            fillData(student);
        }
    }

    private void fillData(Student student) {
        nameField.setText(student.getFullName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
        if (student.getDateOfBirth() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dobField.setText(sdf.format(student.getDateOfBirth()));
        }
        addressField.setText(student.getAddress());
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
            return false;
        }
        
        if (!dobField.getText().trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                sdf.parse(dobField.getText().trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ! (dd/MM/yyyy)");
                return false;
            }
        }
        
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Student getStudent() {
        Date dob = null;
        if (!dobField.getText().trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date parsed = sdf.parse(dobField.getText().trim());
                dob = new Date(parsed.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return new Student(
            nameField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim(),
            dob,
            addressField.getText().trim()
        );
    }
}

