package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Class quản lý kết nối database
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String dbType;
    private Properties properties;

    private DatabaseConnection() {
        loadProperties();
        connect();
        initializeDatabase();
    }

    /**
     * Singleton pattern - chỉ có 1 instance duy nhất
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Đọc file cấu hình database
     */
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/database.properties")) {
            if (input == null) {
                System.out.println("Không tìm thấy file database.properties, sử dụng SQLite mặc định");
                dbType = "sqlite";
                return;
            }
            properties.load(input);
            dbType = properties.getProperty("db.type", "sqlite");
        } catch (IOException e) {
            System.err.println("Lỗi đọc file cấu hình: " + e.getMessage());
            dbType = "sqlite";
        }
    }

    /**
     * Kết nối đến database
     */
    private void connect() {
        try {
            if ("sqlite".equalsIgnoreCase(dbType)) {
                connectSQLite();
            } else if ("mysql".equalsIgnoreCase(dbType)) {
                connectMySQL();
            }
            System.out.println("Kết nối database thành công!");
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kết nối SQLite
     */
    private void connectSQLite() throws SQLException {
        String dbPath = properties.getProperty("db.sqlite.path", "course_management.db");
        String url = "jdbc:sqlite:" + dbPath;
        connection = DriverManager.getConnection(url);
        // Bật foreign key cho SQLite
        Statement stmt = connection.createStatement();
        stmt.execute("PRAGMA foreign_keys = ON");
        stmt.close();
    }

    /**
     * Kết nối MySQL
     */
    private void connectMySQL() throws SQLException {
        String host = properties.getProperty("db.mysql.host", "localhost");
        String port = properties.getProperty("db.mysql.port", "3306");
        String database = properties.getProperty("db.mysql.database", "course_management");
        String username = properties.getProperty("db.mysql.username", "root");
        String password = properties.getProperty("db.mysql.password", "");
        String useSSL = properties.getProperty("db.mysql.useSSL", "false");
        String serverTimezone = properties.getProperty("db.mysql.serverTimezone", "UTC");

        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=%s",
                host, port, database, useSSL, serverTimezone);
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Khởi tạo database với schema nếu chưa có
     */
    private void initializeDatabase() {
        if ("sqlite".equalsIgnoreCase(dbType)) {
            try {
                DatabaseInitializer.initializeSQLite(connection);
                System.out.println("Database đã được khởi tạo!");
            } catch (SQLException e) {
                System.err.println("Lỗi khởi tạo database: " + e.getMessage());
            }
        }
    }

    /**
     * Lấy connection
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra connection: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Đóng connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối database");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đóng connection: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra loại database đang sử dụng
     */
    public String getDbType() {
        return dbType;
    }
}

