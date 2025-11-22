package gui;

import dao.CourseDAO;
import dao.InstructorDAO;
import model.Course;
import model.Instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý khóa học
 */
public class CoursePanel extends JPanel {
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private CourseDAO courseDAO;
    private InstructorDAO instructorDAO;
    private JTextField searchField;

    public CoursePanel() {
        courseDAO = new CourseDAO();
        instructorDAO = new InstructorDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadCourses();
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
        searchButton.addActionListener(e -> searchCourses());
        searchPanel.add(searchButton);
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.addActionListener(e -> loadCourses());
        searchPanel.add(refreshButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Thêm");
        addButton.addActionListener(e -> addCourse());
        JButton editButton = new JButton("✏️ Sửa");
        editButton.addActionListener(e -> editCourse());
        JButton deleteButton = new JButton("🗑️ Xóa");
        deleteButton.addActionListener(e -> deleteCourse());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // Bảng khóa học
        String[] columnNames = {"ID", "Mã khóa học", "Tên khóa học", "Giảng viên", 
                               "Thời lượng (tuần)", "Giá (VNĐ)", "Số SV tối đa", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        List<Course> courses = courseDAO.findAll();
        for (Course course : courses) {
            Object[] row = {
                course.getCourseId(),
                course.getCourseCode(),
                course.getCourseName(),
                course.getInstructorName(),
                course.getDurationWeeks(),
                String.format("%,.0f", course.getPrice()),
                course.getMaxStudents(),
                course.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void searchCourses() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadCourses();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Course> courses = courseDAO.search(keyword);
        for (Course course : courses) {
            Object[] row = {
                course.getCourseId(),
                course.getCourseCode(),
                course.getCourseName(),
                course.getInstructorName(),
                course.getDurationWeeks(),
                String.format("%,.0f", course.getPrice()),
                course.getMaxStudents(),
                course.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void addCourse() {
        CourseDialog dialog = new CourseDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                               "Thêm khóa học mới", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Course course = dialog.getCourse();
            if (courseDAO.insert(course)) {
                JOptionPane.showMessageDialog(this, "Thêm khóa học thành công!");
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm khóa học!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học cần sửa!");
            return;
        }

        int courseId = (int) tableModel.getValueAt(selectedRow, 0);
        Course course = courseDAO.findById(courseId);
        
        CourseDialog dialog = new CourseDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                               "Sửa thông tin khóa học", course);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Course updatedCourse = dialog.getCourse();
            updatedCourse.setCourseId(courseId);
            if (courseDAO.update(updatedCourse)) {
                JOptionPane.showMessageDialog(this, "Cập nhật khóa học thành công!");
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật khóa học!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa khóa học này?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int courseId = (int) tableModel.getValueAt(selectedRow, 0);
            if (courseDAO.delete(courseId)) {
                JOptionPane.showMessageDialog(this, "Xóa khóa học thành công!");
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa khóa học!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

/**
 * Dialog thêm/sửa khóa học
 */
class CourseDialog extends JDialog {
    private JTextField codeField, nameField, durationField, priceField, maxStudentsField;
    private JTextArea descriptionArea;
    private JComboBox<Instructor> instructorCombo;
    private JComboBox<String> statusCombo;
    private boolean confirmed = false;
    private InstructorDAO instructorDAO;

    public CourseDialog(Frame parent, String title, Course course) {
        super(parent, title, true);
        instructorDAO = new InstructorDAO();
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mã khóa học
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã khóa học:"), gbc);
        gbc.gridx = 1;
        codeField = new JTextField(20);
        formPanel.add(codeField, gbc);

        // Tên khóa học
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tên khóa học:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Mô tả
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        // Giảng viên
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Giảng viên:"), gbc);
        gbc.gridx = 1;
        instructorCombo = new JComboBox<>();
        loadInstructors();
        formPanel.add(instructorCombo, gbc);

        // Thời lượng
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Thời lượng (tuần):"), gbc);
        gbc.gridx = 1;
        durationField = new JTextField(20);
        formPanel.add(durationField, gbc);

        // Giá
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Giá (VNĐ):"), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(20);
        formPanel.add(priceField, gbc);

        // Số sinh viên tối đa
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Số SV tối đa:"), gbc);
        gbc.gridx = 1;
        maxStudentsField = new JTextField(20);
        formPanel.add(maxStudentsField, gbc);

        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"active", "inactive", "completed"});
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

        // Nếu đang sửa, điền dữ liệu
        if (course != null) {
            fillData(course);
        }
    }

    private void loadInstructors() {
        List<Instructor> instructors = instructorDAO.findAll();
        for (Instructor instructor : instructors) {
            instructorCombo.addItem(instructor);
        }
    }

    private void fillData(Course course) {
        codeField.setText(course.getCourseCode());
        nameField.setText(course.getCourseName());
        descriptionArea.setText(course.getDescription());
        durationField.setText(String.valueOf(course.getDurationWeeks()));
        priceField.setText(String.valueOf(course.getPrice()));
        maxStudentsField.setText(String.valueOf(course.getMaxStudents()));
        statusCombo.setSelectedItem(course.getStatus());
        
        // Chọn giảng viên
        for (int i = 0; i < instructorCombo.getItemCount(); i++) {
            if (instructorCombo.getItemAt(i).getInstructorId() == course.getInstructorId()) {
                instructorCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private boolean validateInput() {
        if (codeField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
            return false;
        }
        
        try {
            Integer.parseInt(durationField.getText().trim());
            Double.parseDouble(priceField.getText().trim());
            Integer.parseInt(maxStudentsField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu số không hợp lệ!");
            return false;
        }
        
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Course getCourse() {
        Instructor selectedInstructor = (Instructor) instructorCombo.getSelectedItem();
        return new Course(
            nameField.getText().trim(),
            codeField.getText().trim(),
            descriptionArea.getText().trim(),
            selectedInstructor.getInstructorId(),
            Integer.parseInt(durationField.getText().trim()),
            Double.parseDouble(priceField.getText().trim()),
            Integer.parseInt(maxStudentsField.getText().trim()),
            (String) statusCombo.getSelectedItem()
        );
    }
}

