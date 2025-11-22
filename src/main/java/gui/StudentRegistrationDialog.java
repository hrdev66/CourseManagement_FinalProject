package gui;

import dao.StudentDAO;
import dao.UserDAO;
import model.Student;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Dialog đăng ký tài khoản Sinh viên mới
 * Kết hợp đăng ký tài khoản và tạo thông tin sinh viên
 */
public class StudentRegistrationDialog extends JDialog {
    private JTextField usernameField, emailField, fullNameField, phoneField, addressField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextField dobField;
    private JButton registerButton, cancelButton;
    private UserDAO userDAO;
    private StudentDAO studentDAO;
    private boolean registered = false;

    public StudentRegistrationDialog(Frame parent) {
        super(parent, "Đăng ký tài khoản Sinh viên", true);
        userDAO = new UserDAO();
        studentDAO = new StudentDAO();
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {
        // Panel chính với scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("🎓 Đăng ký tài khoản Sinh viên mới");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel);

        // Panel form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Thông tin đăng nhập
        JLabel loginInfoLabel = new JLabel("📝 Thông tin đăng nhập:");
        loginInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(loginInfoLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tên đăng nhập:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(25);
        formPanel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(25);
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Mật khẩu:*"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(25);
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Xác nhận mật khẩu:*"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(25);
        formPanel.add(confirmPasswordField, gbc);

        // Thông tin cá nhân
        JLabel personalInfoLabel = new JLabel("👤 Thông tin cá nhân:");
        personalInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        formPanel.add(personalInfoLabel, gbc);

        // Full Name
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Họ tên:*"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(25);
        formPanel.add(fullNameField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(25);
        formPanel.add(phoneField, gbc);

        // Date of Birth
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Ngày sinh (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        dobField = new JTextField(25);
        formPanel.add(dobField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(25);
        formPanel.add(addressField, gbc);

        // Note
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        JLabel noteLabel = new JLabel("<html><small><b>Lưu ý:</b> (*) = Bắt buộc<br>" +
                                      "Tài khoản đăng ký mới mặc định là <b>Sinh viên</b>.</small></html>");
        noteLabel.setForeground(Color.GRAY);
        formPanel.add(noteLabel, gbc);

        mainPanel.add(formPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        registerButton = new JButton("💾 Đăng ký");
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.addActionListener(e -> performRegister());
        
        cancelButton = new JButton("❌ Hủy");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void performRegister() {
        // Lấy thông tin
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String dobStr = dobField.getText().trim();
        String address = addressField.getText().trim();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng điền đầy đủ các trường bắt buộc (*)!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        // Kiểm tra username đã tồn tại
        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!");
            return;
        }

        // Kiểm tra email đã tồn tại
        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email đã được sử dụng!");
            return;
        }

        // Parse date of birth
        Date dob = null;
        if (!dobStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                java.util.Date parsed = sdf.parse(dobStr);
                dob = new Date(parsed.getTime());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ! (dd/MM/yyyy)");
                return;
            }
        }

        // Tạo Student record trước
        Student student = new Student(fullName, email, phone, dob, address);
        if (!studentDAO.insert(student)) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi tạo thông tin sinh viên!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy student_id vừa tạo
        int studentId = student.getStudentId();

        // Tạo User account với role = student và reference_id = studentId
        User newUser = new User(username, password, email, "student");
        newUser.setReferenceId(studentId);

        if (userDAO.register(newUser)) {
            registered = true;
            JOptionPane.showMessageDialog(this, 
                "Đăng ký thành công!\n\n" +
                "Tài khoản của bạn đã được tạo với quyền Sinh viên.\n" +
                "Bạn có thể đăng nhập ngay bây giờ.\n\n" +
                "Tên đăng nhập: " + username, 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            // Nếu tạo user thất bại, xóa student record
            studentDAO.delete(studentId);
            JOptionPane.showMessageDialog(this, 
                "Lỗi đăng ký tài khoản!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isRegistered() {
        return registered;
    }
}

