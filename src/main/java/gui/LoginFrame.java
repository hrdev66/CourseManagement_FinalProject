package gui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Màn hình đăng nhập
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private UserDAO userDAO;
    private User currentUser;

    public LoginFrame() {
        userDAO = new UserDAO();
        
        setTitle("Đăng nhập - Hệ thống Quản lý Khóa học");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(240, 240, 240));
        
        // Panel chính
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("🎓 Đăng nhập hệ thống", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Mật khẩu:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(passwordField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        loginButton = new JButton("🔐 Đăng nhập");
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.addActionListener(e -> performLogin());
        
        registerButton = new JButton("📝 Đăng ký");
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.addActionListener(e -> showRegisterDialog());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        mainPanel.add(buttonPanel, gbc);

        // Enter key để đăng nhập
        getRootPane().setDefaultButton(loginButton);
        passwordField.addActionListener(e -> performLogin());

        add(mainPanel, BorderLayout.CENTER);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Đăng nhập
        currentUser = userDAO.login(username, password);

        if (currentUser != null) {
            // Đăng nhập thành công
            JOptionPane.showMessageDialog(this, 
                "Đăng nhập thành công!\nXin chào " + currentUser.getFullName(), 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Mở MainFrame với quyền của user
            this.dispose();
            MainFrame mainFrame = new MainFrame(currentUser);
            mainFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Tên đăng nhập hoặc mật khẩu không đúng!", 
                "Lỗi đăng nhập", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void showRegisterDialog() {
        // Sử dụng StudentRegistrationDialog mới (đăng ký đầy đủ)
        StudentRegistrationDialog dialog = new StudentRegistrationDialog(this);
        dialog.setVisible(true);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        // Sử dụng Look and Feel của hệ thống
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy ứng dụng trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}


