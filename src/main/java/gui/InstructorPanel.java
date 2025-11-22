package gui;

import dao.InstructorDAO;
import model.Instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý giảng viên
 */
public class InstructorPanel extends JPanel {
    private JTable instructorTable;
    private DefaultTableModel tableModel;
    private InstructorDAO instructorDAO;
    private JTextField searchField;

    public InstructorPanel() {
        instructorDAO = new InstructorDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadInstructors();
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
        searchButton.addActionListener(e -> searchInstructors());
        searchPanel.add(searchButton);
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.addActionListener(e -> loadInstructors());
        searchPanel.add(refreshButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Thêm");
        addButton.addActionListener(e -> addInstructor());
        JButton editButton = new JButton("✏️ Sửa");
        editButton.addActionListener(e -> editInstructor());
        JButton deleteButton = new JButton("🗑️ Xóa");
        deleteButton.addActionListener(e -> deleteInstructor());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // Bảng giảng viên
        String[] columnNames = {"ID", "Họ tên", "Email", "Số điện thoại", "Chuyên môn", "Tiểu sử"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        instructorTable = new JTable(tableModel);
        instructorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        instructorTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(instructorTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadInstructors() {
        tableModel.setRowCount(0);
        List<Instructor> instructors = instructorDAO.findAll();
        
        for (Instructor instructor : instructors) {
            Object[] row = {
                instructor.getInstructorId(),
                instructor.getFullName(),
                instructor.getEmail(),
                instructor.getPhone(),
                instructor.getSpecialization(),
                instructor.getBio()
            };
            tableModel.addRow(row);
        }
    }

    private void searchInstructors() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadInstructors();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Instructor> instructors = instructorDAO.search(keyword);
        
        for (Instructor instructor : instructors) {
            Object[] row = {
                instructor.getInstructorId(),
                instructor.getFullName(),
                instructor.getEmail(),
                instructor.getPhone(),
                instructor.getSpecialization(),
                instructor.getBio()
            };
            tableModel.addRow(row);
        }
    }

    private void addInstructor() {
        InstructorDialog dialog = new InstructorDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                       "Thêm giảng viên mới", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Instructor instructor = dialog.getInstructor();
            if (instructorDAO.insert(instructor)) {
                JOptionPane.showMessageDialog(this, "Thêm giảng viên thành công!");
                loadInstructors();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm giảng viên!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editInstructor() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giảng viên cần sửa!");
            return;
        }

        int instructorId = (int) tableModel.getValueAt(selectedRow, 0);
        Instructor instructor = instructorDAO.findById(instructorId);
        
        InstructorDialog dialog = new InstructorDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                                       "Sửa thông tin giảng viên", instructor);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Instructor updatedInstructor = dialog.getInstructor();
            updatedInstructor.setInstructorId(instructorId);
            if (instructorDAO.update(updatedInstructor)) {
                JOptionPane.showMessageDialog(this, "Cập nhật giảng viên thành công!");
                loadInstructors();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật giảng viên!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteInstructor() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giảng viên cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa giảng viên này?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int instructorId = (int) tableModel.getValueAt(selectedRow, 0);
            if (instructorDAO.delete(instructorId)) {
                JOptionPane.showMessageDialog(this, "Xóa giảng viên thành công!");
                loadInstructors();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa giảng viên!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

/**
 * Dialog thêm/sửa giảng viên
 */
class InstructorDialog extends JDialog {
    private JTextField nameField, emailField, phoneField, specializationField;
    private JTextArea bioArea;
    private boolean confirmed = false;

    public InstructorDialog(Frame parent, String title, Instructor instructor) {
        super(parent, title, true);
        
        setSize(450, 450);
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

        // Chuyên môn
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Chuyên môn:"), gbc);
        gbc.gridx = 1;
        specializationField = new JTextField(20);
        formPanel.add(specializationField, gbc);

        // Tiểu sử
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Tiểu sử:"), gbc);
        gbc.gridx = 1;
        bioArea = new JTextArea(5, 20);
        bioArea.setLineWrap(true);
        JScrollPane bioScrollPane = new JScrollPane(bioArea);
        formPanel.add(bioScrollPane, gbc);

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
        if (instructor != null) {
            fillData(instructor);
        }
    }

    private void fillData(Instructor instructor) {
        nameField.setText(instructor.getFullName());
        emailField.setText(instructor.getEmail());
        phoneField.setText(instructor.getPhone());
        specializationField.setText(instructor.getSpecialization());
        bioArea.setText(instructor.getBio());
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Instructor getInstructor() {
        return new Instructor(
            nameField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim(),
            specializationField.getText().trim(),
            bioArea.getText().trim()
        );
    }
}

