-- Script khởi tạo database cho MariaDB
-- Chạy script này trước khi chạy ứng dụng

-- Xóa database nếu tồn tại
DROP DATABASE IF EXISTS course_management;

-- Tạo database mới
CREATE DATABASE course_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Sử dụng database
USE course_management;

-- Bảng Giảng viên (Instructors)
CREATE TABLE instructors (
    instructor_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    specialization VARCHAR(100),
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Khóa học (Courses)
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(200) NOT NULL,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    description TEXT,
    instructor_id INT,
    duration_weeks INT,
    price DECIMAL(10, 2),
    max_students INT DEFAULT 50,
    status ENUM('active', 'inactive', 'completed') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Sinh viên (Students)
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    address TEXT,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Đăng ký khóa học (Enrollments)
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    completion_status ENUM('enrolled', 'in_progress', 'completed', 'dropped') DEFAULT 'enrolled',
    grade DECIMAL(5, 2),
    payment_status ENUM('pending', 'paid', 'refunded') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Bài học (Lessons)
CREATE TABLE lessons (
    lesson_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    lesson_title VARCHAR(200) NOT NULL,
    lesson_order INT NOT NULL,
    content TEXT,
    video_url VARCHAR(500),
    duration_minutes INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Đánh giá (Reviews)
CREATE TABLE reviews (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    student_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Bài tập (Assignments)
CREATE TABLE assignments (
    assignment_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    due_date DATE,
    max_score INT DEFAULT 100,
    assignment_type ENUM('homework', 'quiz', 'project') DEFAULT 'homework',
    status ENUM('published', 'draft') DEFAULT 'published',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Bài nộp (Submissions)
CREATE TABLE submissions (
    submission_id INT PRIMARY KEY AUTO_INCREMENT,
    assignment_id INT NOT NULL,
    student_id INT NOT NULL,
    content TEXT,
    attachment VARCHAR(500),
    score INT,
    status ENUM('submitted', 'graded', 'late') DEFAULT 'submitted',
    submitted_date DATE DEFAULT (CURRENT_DATE),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignment_id) REFERENCES assignments(assignment_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Thông báo (Announcements)
CREATE TABLE announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    instructor_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    priority ENUM('normal', 'important', 'urgent') DEFAULT 'normal',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Users/Tài khoản (Authentication & Authorization)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Hashed password (MD5)
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('ADMIN', 'INSTRUCTOR', 'STUDENT') NOT NULL,
    reference_id INT DEFAULT 0, -- ID của instructor hoặc student (0 nếu admin)
    -- Không dùng FOREIGN KEY vì reference_id có thể tham chiếu đến 2 bảng khác nhau
    -- (instructors hoặc students) tùy theo role
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Thêm dữ liệu mẫu

-- Giảng viên mẫu
INSERT INTO instructors (full_name, email, phone, specialization, bio) VALUES
('Nguyễn Văn An', 'nva@email.com', '0901234567', 'Lập trình Java', 'Giảng viên với 10 năm kinh nghiệm'),
('Trần Thị Bình', 'ttb@email.com', '0902345678', 'Web Development', 'Chuyên gia phát triển web'),
('Lê Văn Cường', 'lvc@email.com', '0903456789', 'Data Science', 'Tiến sĩ Khoa học Dữ liệu'),
('Phạm Thị Dung', 'ptd@email.com', '0904567890', 'Mobile Development', 'Chuyên gia phát triển ứng dụng di động');

-- Khóa học mẫu
INSERT INTO courses (course_name, course_code, description, instructor_id, duration_weeks, price, max_students, status) VALUES
('Lập trình Java cơ bản', 'JAVA101', 'Khóa học Java từ cơ bản đến nâng cao', 1, 12, 2500000, 30, 'active'),
('Phát triển Web với Spring Boot', 'WEB201', 'Xây dựng ứng dụng web với Spring Boot', 2, 16, 3500000, 25, 'active'),
('Python cho Data Science', 'DS301', 'Phân tích dữ liệu với Python', 3, 14, 4000000, 20, 'active'),
('Phát triển ứng dụng Android', 'MOB401', 'Lập trình Android từ đầu', 4, 18, 4500000, 15, 'active'),
('JavaScript và React', 'WEB301', 'Xây dựng giao diện web hiện đại', 2, 10, 3000000, 30, 'active');

-- Sinh viên mẫu
INSERT INTO students (full_name, email, phone, date_of_birth, address) VALUES
('Hoàng Văn Minh', 'hvm@student.com', '0911111111', '2002-05-15', 'Hà Nội'),
('Đỗ Thị Lan', 'dtl@student.com', '0922222222', '2003-08-20', 'Hồ Chí Minh'),
('Vũ Văn Nam', 'vvn@student.com', '0933333333', '2002-12-10', 'Đà Nẵng'),
('Bùi Thị Hoa', 'bth@student.com', '0944444444', '2003-03-25', 'Hải Phòng'),
('Đặng Văn Tuấn', 'dvt@student.com', '0955555555', '2002-07-30', 'Cần Thơ');

-- Đăng ký khóa học mẫu
INSERT INTO enrollments (student_id, course_id, completion_status, grade, payment_status) VALUES
(1, 1, 'completed', 8.5, 'paid'),
(1, 2, 'in_progress', NULL, 'paid'),
(2, 1, 'in_progress', NULL, 'paid'),
(2, 3, 'enrolled', NULL, 'pending'),
(3, 4, 'in_progress', NULL, 'paid'),
(4, 2, 'completed', 9.0, 'paid'),
(5, 5, 'in_progress', NULL, 'paid');

-- Bài học mẫu
INSERT INTO lessons (course_id, lesson_title, lesson_order, content, duration_minutes) VALUES
(1, 'Giới thiệu về Java', 1, 'Tổng quan về ngôn ngữ lập trình Java', 45),
(1, 'Biến và kiểu dữ liệu', 2, 'Các kiểu dữ liệu cơ bản trong Java', 60),
(1, 'Cấu trúc điều khiển', 3, 'If-else, switch, loops', 75),
(2, 'Giới thiệu Spring Boot', 1, 'Khởi tạo dự án Spring Boot', 50),
(2, 'REST API với Spring', 2, 'Xây dựng RESTful API', 90);

-- Đánh giá mẫu
INSERT INTO reviews (course_id, student_id, rating, comment) VALUES
(1, 1, 5, 'Khóa học rất tuyệt vời, giảng viên nhiệt tình'),
(2, 4, 5, 'Nội dung chi tiết, dễ hiểu'),
(1, 2, 4, 'Khóa học tốt nhưng cần thêm bài tập thực hành');

-- Bài tập mẫu
INSERT INTO assignments (course_id, title, description, due_date, max_score, assignment_type, status) VALUES
(1, 'Bài tập 1: Làm quen với Java', 'Viết chương trình Hello World và các ví dụ cơ bản', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 100, 'homework', 'published'),
(1, 'Bài tập 2: Xử lý mảng', 'Thực hành với mảng một chiều và hai chiều', DATE_ADD(CURDATE(), INTERVAL 14 DAY), 100, 'homework', 'published'),
(2, 'Quiz 1: Spring Boot Basics', 'Câu hỏi trắc nghiệm về Spring Boot', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 50, 'quiz', 'published'),
(2, 'Dự án cuối kỳ: Web Application', 'Xây dựng ứng dụng web hoàn chỉnh', DATE_ADD(CURDATE(), INTERVAL 60 DAY), 200, 'project', 'published'),
(3, 'Bài tập Data Analysis', 'Phân tích dữ liệu với Pandas', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 150, 'homework', 'published');

-- Bài nộp mẫu
INSERT INTO submissions (assignment_id, student_id, content, status, submitted_date) VALUES
(1, 1, 'Đã hoàn thành bài tập Hello World và các ví dụ cơ bản', 'graded', CURDATE()),
(1, 2, 'Bài tập đã hoàn thành', 'submitted', CURDATE()),
(2, 1, 'Đã làm bài tập mảng', 'submitted', CURDATE());

-- Cập nhật điểm cho bài nộp đã chấm
UPDATE submissions SET score = 95 WHERE submission_id = 1;

-- Thông báo mẫu
INSERT INTO announcements (course_id, instructor_id, title, content, priority) VALUES
(1, 1, 'Chào mừng các bạn đến với khóa học Java', 'Chào mừng các bạn đã đăng ký khóa học Lập trình Java cơ bản. Chúng ta sẽ bắt đầu vào tuần tới!', 'normal'),
(1, 1, 'Thông báo về lịch thi', 'Lịch thi giữa kỳ sẽ được thông báo sớm. Các bạn chú ý theo dõi!', 'important'),
(2, 2, 'Tài liệu học tập đã được cập nhật', 'Các tài liệu và slide bài giảng đã được cập nhật trên hệ thống. Vui lòng tải về!', 'normal'),
(2, 2, 'Deadline nộp dự án cuối kỳ', 'Nhắc nhở: Dự án cuối kỳ cần nộp trước ngày 31/12. Các bạn chú ý!', 'urgent');

-- Tài khoản mẫu
-- Password mặc định cho tất cả: "123456"
-- Admin: admin / 123456
-- Instructors: Tên đăng nhập là email (nva, ttb, lvc, ptd) / 123456
-- Students: Tên đăng nhập là email (hvm, dtl, vvn, bth, dvt) / 123456

INSERT INTO users (username, password, email, role, reference_id) VALUES
-- Admin
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@course.com', 'ADMIN', 0),
-- Giảng viên (reference_id = instructor_id)
('nva', 'e10adc3949ba59abbe56e057f20f883e', 'nva@email.com', 'INSTRUCTOR', 1),
('ttb', 'e10adc3949ba59abbe56e057f20f883e', 'ttb@email.com', 'INSTRUCTOR', 2),
('lvc', 'e10adc3949ba59abbe56e057f20f883e', 'lvc@email.com', 'INSTRUCTOR', 3),
('ptd', 'e10adc3949ba59abbe56e057f20f883e', 'ptd@email.com', 'INSTRUCTOR', 4),
-- Sinh viên (reference_id = student_id)
('hvm', 'e10adc3949ba59abbe56e057f20f883e', 'hvm@student.com', 'STUDENT', 1),
('dtl', 'e10adc3949ba59abbe56e057f20f883e', 'dtl@student.com', 'STUDENT', 2),
('vvn', 'e10adc3949ba59abbe56e057f20f883e', 'vvn@student.com', 'STUDENT', 3),
('bth', 'e10adc3949ba59abbe56e057f20f883e', 'bth@student.com', 'STUDENT', 4),
('dvt', 'e10adc3949ba59abbe56e057f20f883e', 'dvt@student.com', 'STUDENT', 5);

-- Hoàn tất
SELECT 'Database course_management đã được tạo thành công!' AS Message;
SELECT 'Dữ liệu mẫu đã được thêm vào!' AS Message;
SELECT 'Tài khoản mẫu: admin/123456, nva/123456, hvm/123456, ...' AS Message;

