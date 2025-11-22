import gui.LoginFrame;

import javax.swing.*;

/**
 * Class chính để khởi chạy ứng dụng
 * Hệ thống Quản lý Khóa học Trực tuyến
 */
public class Main {
    public static void main(String[] args) {
        // Sử dụng Look and Feel của hệ thống
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy màn hình đăng nhập trước
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
