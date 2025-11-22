package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class khởi tạo database schema và dữ liệu mẫu
 */
public class DatabaseInitializer {

    /**
     * Khởi tạo database SQLite
     */
    public static void initializeSQLite(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Tạo bảng instructors
        stmt.execute("CREATE TABLE IF NOT EXISTS instructors (" +
                "instructor_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT, " +
                "specialization TEXT, " +
                "bio TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // Tạo bảng courses
        stmt.execute("CREATE TABLE IF NOT EXISTS courses (" +
                "course_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_name TEXT NOT NULL, " +
                "course_code TEXT UNIQUE NOT NULL, " +
                "description TEXT, " +
                "instructor_id INTEGER, " +
                "duration_weeks INTEGER, " +
                "price REAL, " +
                "max_students INTEGER DEFAULT 50, " +
                "status TEXT DEFAULT 'active' CHECK(status IN ('active', 'inactive', 'completed')), " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id) ON DELETE SET NULL)");

        // Tạo bảng students
        stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                "student_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT, " +
                "date_of_birth DATE, " +
                "address TEXT, " +
                "enrollment_date DATE DEFAULT (date('now')), " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // Tạo bảng enrollments
        stmt.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                "enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER NOT NULL, " +
                "course_id INTEGER NOT NULL, " +
                "enrollment_date DATE DEFAULT (date('now')), " +
                "completion_status TEXT DEFAULT 'enrolled' CHECK(completion_status IN ('enrolled', 'in_progress', 'completed', 'dropped')), " +
                "grade REAL, " +
                "payment_status TEXT DEFAULT 'pending' CHECK(payment_status IN ('pending', 'paid', 'refunded')), " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE, " +
                "UNIQUE(student_id, course_id))");

        // Tạo bảng lessons
        stmt.execute("CREATE TABLE IF NOT EXISTS lessons (" +
                "lesson_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER NOT NULL, " +
                "lesson_title TEXT NOT NULL, " +
                "lesson_order INTEGER NOT NULL, " +
                "content TEXT, " +
                "video_url TEXT, " +
                "duration_minutes INTEGER, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE)");

        // Tạo bảng reviews
        stmt.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                "review_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER NOT NULL, " +
                "student_id INTEGER NOT NULL, " +
                "rating INTEGER CHECK (rating >= 1 AND rating <= 5), " +
                "comment TEXT, " +
                "review_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE)");

        // Tạo bảng assignments
        stmt.execute("CREATE TABLE IF NOT EXISTS assignments (" +
                "assignment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER NOT NULL, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "due_date DATE, " +
                "max_score INTEGER DEFAULT 100, " +
                "assignment_type TEXT DEFAULT 'homework' CHECK(assignment_type IN ('homework', 'quiz', 'project')), " +
                "status TEXT DEFAULT 'published' CHECK(status IN ('published', 'draft')), " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE)");

        // Tạo bảng submissions
        stmt.execute("CREATE TABLE IF NOT EXISTS submissions (" +
                "submission_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "assignment_id INTEGER NOT NULL, " +
                "student_id INTEGER NOT NULL, " +
                "content TEXT, " +
                "attachment TEXT, " +
                "score INTEGER, " +
                "status TEXT DEFAULT 'submitted' CHECK(status IN ('submitted', 'graded', 'late')), " +
                "submitted_date DATE DEFAULT (date('now')), " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (assignment_id) REFERENCES assignments(assignment_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE)");

        // Tạo bảng announcements
        stmt.execute("CREATE TABLE IF NOT EXISTS announcements (" +
                "announcement_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER NOT NULL, " +
                "instructor_id INTEGER NOT NULL, " +
                "title TEXT NOT NULL, " +
                "content TEXT, " +
                "priority TEXT DEFAULT 'normal' CHECK(priority IN ('normal', 'important', 'urgent')), " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id) ON DELETE CASCADE)");

        // Tạo bảng users (không dùng FOREIGN KEY vì reference_id có thể tham chiếu đến 2 bảng)
        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "role TEXT NOT NULL CHECK(role IN ('admin', 'instructor', 'student')), " +
                "reference_id INTEGER DEFAULT 0, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "last_login DATETIME)");

        // Kiểm tra xem đã có dữ liệu chưa
        var rs = stmt.executeQuery("SELECT COUNT(*) as count FROM instructors");
        if (rs.next() && rs.getInt("count") == 0) {
            insertSampleData(stmt);
        }

        stmt.close();
    }

    /**
     * Thêm dữ liệu mẫu
     */
    private static void insertSampleData(Statement stmt) throws SQLException {
        // Thêm giảng viên
        stmt.execute("INSERT INTO instructors (full_name, email, phone, specialization, bio) VALUES " +
                "('Nguyễn Văn An', 'nva@email.com', '0901234567', 'Lập trình Java', 'Giảng viên với 10 năm kinh nghiệm'), " +
                "('Trần Thị Bình', 'ttb@email.com', '0902345678', 'Web Development', 'Chuyên gia phát triển web'), " +
                "('Lê Văn Cường', 'lvc@email.com', '0903456789', 'Data Science', 'Tiến sĩ Khoa học Dữ liệu'), " +
                "('Phạm Thị Dung', 'ptd@email.com', '0904567890', 'Mobile Development', 'Chuyên gia phát triển ứng dụng di động')");

        // Thêm khóa học
        stmt.execute("INSERT INTO courses (course_name, course_code, description, instructor_id, duration_weeks, price, max_students, status) VALUES " +
                "('Lập trình Java cơ bản', 'JAVA101', 'Khóa học Java từ cơ bản đến nâng cao', 1, 12, 2500000, 30, 'active'), " +
                "('Phát triển Web với Spring Boot', 'WEB201', 'Xây dựng ứng dụng web với Spring Boot', 2, 16, 3500000, 25, 'active'), " +
                "('Python cho Data Science', 'DS301', 'Phân tích dữ liệu với Python', 3, 14, 4000000, 20, 'active'), " +
                "('Phát triển ứng dụng Android', 'MOB401', 'Lập trình Android từ đầu', 4, 18, 4500000, 15, 'active'), " +
                "('JavaScript và React', 'WEB301', 'Xây dựng giao diện web hiện đại', 2, 10, 3000000, 30, 'active')");

        // Thêm sinh viên
        stmt.execute("INSERT INTO students (full_name, email, phone, date_of_birth, address) VALUES " +
                "('Hoàng Văn Minh', 'hvm@student.com', '0911111111', '2002-05-15', 'Hà Nội'), " +
                "('Đỗ Thị Lan', 'dtl@student.com', '0922222222', '2003-08-20', 'Hồ Chí Minh'), " +
                "('Vũ Văn Nam', 'vvn@student.com', '0933333333', '2002-12-10', 'Đà Nẵng'), " +
                "('Bùi Thị Hoa', 'bth@student.com', '0944444444', '2003-03-25', 'Hải Phòng'), " +
                "('Đặng Văn Tuấn', 'dvt@student.com', '0955555555', '2002-07-30', 'Cần Thơ')");

        // Thêm đăng ký
        stmt.execute("INSERT INTO enrollments (student_id, course_id, completion_status, grade, payment_status) VALUES " +
                "(1, 1, 'completed', 8.5, 'paid'), " +
                "(1, 2, 'in_progress', NULL, 'paid'), " +
                "(2, 1, 'in_progress', NULL, 'paid'), " +
                "(2, 3, 'enrolled', NULL, 'pending'), " +
                "(3, 4, 'in_progress', NULL, 'paid'), " +
                "(4, 2, 'completed', 9.0, 'paid'), " +
                "(5, 5, 'in_progress', NULL, 'paid')");

        // Thêm bài học
        stmt.execute("INSERT INTO lessons (course_id, lesson_title, lesson_order, content, duration_minutes) VALUES " +
                "(1, 'Giới thiệu về Java', 1, 'Tổng quan về ngôn ngữ lập trình Java', 45), " +
                "(1, 'Biến và kiểu dữ liệu', 2, 'Các kiểu dữ liệu cơ bản trong Java', 60), " +
                "(1, 'Cấu trúc điều khiển', 3, 'If-else, switch, loops', 75), " +
                "(2, 'Giới thiệu Spring Boot', 1, 'Khởi tạo dự án Spring Boot', 50), " +
                "(2, 'REST API với Spring', 2, 'Xây dựng RESTful API', 90)");

        // Thêm đánh giá
        stmt.execute("INSERT INTO reviews (course_id, student_id, rating, comment) VALUES " +
                "(1, 1, 5, 'Khóa học rất tuyệt vời, giảng viên nhiệt tình'), " +
                "(2, 4, 5, 'Nội dung chi tiết, dễ hiểu'), " +
                "(1, 2, 4, 'Khóa học tốt nhưng cần thêm bài tập thực hành')");

        // Thêm bài tập (assignments)
        stmt.execute("INSERT INTO assignments (course_id, title, description, due_date, max_score, assignment_type, status) VALUES " +
                "(1, 'Bài tập 1: Làm quen với Java', 'Viết chương trình Hello World và các ví dụ cơ bản', date('now', '+7 days'), 100, 'homework', 'published'), " +
                "(1, 'Bài tập 2: Xử lý mảng', 'Thực hành với mảng một chiều và hai chiều', date('now', '+14 days'), 100, 'homework', 'published'), " +
                "(2, 'Quiz 1: Spring Boot Basics', 'Câu hỏi trắc nghiệm về Spring Boot', date('now', '+5 days'), 50, 'quiz', 'published'), " +
                "(2, 'Dự án cuối kỳ: Web Application', 'Xây dựng ứng dụng web hoàn chỉnh', date('now', '+60 days'), 200, 'project', 'published'), " +
                "(3, 'Bài tập Data Analysis', 'Phân tích dữ liệu với Pandas', date('now', '+10 days'), 150, 'homework', 'published')");

        // Thêm bài nộp (submissions)
        stmt.execute("INSERT INTO submissions (assignment_id, student_id, content, status, submitted_date) VALUES " +
                "(1, 1, 'Đã hoàn thành bài tập Hello World và các ví dụ cơ bản', 'graded', date('now')), " +
                "(1, 2, 'Bài tập đã hoàn thành', 'submitted', date('now')), " +
                "(2, 1, 'Đã làm bài tập mảng', 'submitted', date('now'))");

        // Cập nhật điểm cho bài nộp đã chấm
        stmt.execute("UPDATE submissions SET score = 95 WHERE submission_id = 1");

        // Thêm thông báo (announcements)
        stmt.execute("INSERT INTO announcements (course_id, instructor_id, title, content, priority) VALUES " +
                "(1, 1, 'Chào mừng các bạn đến với khóa học Java', 'Chào mừng các bạn đã đăng ký khóa học Lập trình Java cơ bản. Chúng ta sẽ bắt đầu vào tuần tới!', 'normal'), " +
                "(1, 1, 'Thông báo về lịch thi', 'Lịch thi giữa kỳ sẽ được thông báo sớm. Các bạn chú ý theo dõi!', 'important'), " +
                "(2, 2, 'Tài liệu học tập đã được cập nhật', 'Các tài liệu và slide bài giảng đã được cập nhật trên hệ thống. Vui lòng tải về!', 'normal'), " +
                "(2, 2, 'Deadline nộp dự án cuối kỳ', 'Nhắc nhở: Dự án cuối kỳ cần nộp trước ngày 31/12. Các bạn chú ý!', 'urgent')");

        // Thêm tài khoản mẫu (password: 123456 -> MD5: e10adc3949ba59abbe56e057f20f883e)
        stmt.execute("INSERT INTO users (username, password, email, role, reference_id) VALUES " +
                "('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@course.com', 'admin', 0), " +
                "('nva', 'e10adc3949ba59abbe56e057f20f883e', 'nva@email.com', 'instructor', 1), " +
                "('ttb', 'e10adc3949ba59abbe56e057f20f883e', 'ttb@email.com', 'instructor', 2), " +
                "('lvc', 'e10adc3949ba59abbe56e057f20f883e', 'lvc@email.com', 'instructor', 3), " +
                "('ptd', 'e10adc3949ba59abbe56e057f20f883e', 'ptd@email.com', 'instructor', 4), " +
                "('hvm', 'e10adc3949ba59abbe56e057f20f883e', 'hvm@student.com', 'student', 1), " +
                "('dtl', 'e10adc3949ba59abbe56e057f20f883e', 'dtl@student.com', 'student', 2), " +
                "('vvn', 'e10adc3949ba59abbe56e057f20f883e', 'vvn@student.com', 'student', 3), " +
                "('bth', 'e10adc3949ba59abbe56e057f20f883e', 'bth@student.com', 'student', 4), " +
                "('dvt', 'e10adc3949ba59abbe56e057f20f883e', 'dvt@student.com', 'student', 5)");

        System.out.println("Đã thêm dữ liệu mẫu vào database!");
        System.out.println("Tài khoản mẫu:");
        System.out.println("  - Admin: admin / 123456");
        System.out.println("  - Giảng viên: nva / 123456, ttb / 123456, ...");
        System.out.println("  - Sinh viên: hvm / 123456, dtl / 123456, ...");
    }
}

